package com.tpi.service;

import com.tpi.model.Tarifa;
import com.tpi.model.Tramo;
import com.tpi.model.Camion;
import com.tpi.model.Ruta;
import com.tpi.client.SolicitudClient;
import com.tpi.dto.CostoFinalDTO;
import com.tpi.dto.external.ContenedorResponseDTO;
import com.tpi.dto.response.CostosEstimadosDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CalculoCostoService {
    
    private final CamionService camionService;
    private final SolicitudClient solicitudesClient;
    private final TramoService tramoService;

    // private final SolicitudClient solicitudClient; // Para obtener info del contenedor
    
    /**
     * Calcula el costo final de una ruta completa basado en todos sus tramos.
     * Sigue la fórmula del enunciado:
     * Cargos Gestión + Costo por km camión + Costo combustible + Costo estadía
     * 
     * @param rutaId ID de la ruta a calcular
     * @return DTO con el desglose completo de costos
     */
    public CostoFinalDTO calcularCostoFinalRuta(Ruta ruta) {
        
        // 1. Obtener ruta con tramos
        List<Tramo> tramos = tramoService.tramosPorRutaId(ruta.getId());
        if (tramos.isEmpty()) {
            throw new IllegalStateException("La ruta no tiene tramos asignados");
        }
        
        // 2. Obtener precios fijos de la tarifa
        Tarifa tarifa = ruta.getTarifa();
        Double precioCombustible = tarifa.getPrecioCombustiblePorLitro();
        Double costoGestionPorTramo = tarifa.getCostoGestionPorTramo();
        
        // 3. Inicializar acumuladores
        Double costoGestionTotal = 0.0;
        Double costoCamionTotal = 0.0;
        Double costoCombustibleTotal = 0.0;
        Double costoEstadiaTotal = 0.0;
        Long tiempoTotalSegundos = 0L;
        
        // 4. Procesar cada tramo de la ruta
        for (Tramo tramo : tramos) {
            log.debug("Procesando tramo ID: {} - Estado: {}", tramo.getId(), tramo.getEstado().getNombre());
            
            // A. CARGO GESTIÓN (valor fijo por cada tramo)
            costoGestionTotal += costoGestionPorTramo;
            log.debug("Costo gestión tramo {}: ${}", tramo.getId(), costoGestionPorTramo);
            
            // B. COSTO POR KILÓMETRO DE CADA CAMIÓN (solo si tiene camión asignado)
            if (tramo.getCamion() != null && tramo.getDistanciaKm() != null) {
                Double costoTramoCamion = tramo.getDistanciaKm() * tramo.getCamion().getCostoPorKm();
                costoCamionTotal += costoTramoCamion;

                log.debug("Costo camión tramo {}: ${} ({} km × ${}/km)", 
                    tramo.getId(), costoTramoCamion, tramo.getDistanciaKm(), tramo.getCamion().getCostoPorKm());
            }
            
            // C. COSTO COMBUSTIBLE (consumo camión × valor litro)
            if (tramo.getCamion() != null && tramo.getDistanciaKm() != null) {
                // Calcular litros consumidos: distancia × (consumo/100)
                Double litrosConsumidos = tramo.getDistanciaKm() * 
                                        (tramo.getCamion().getConsumoCombustibleLx100km() / 100);
                
                Double costoCombustibleTramo = litrosConsumidos * precioCombustible;
                costoCombustibleTotal += costoCombustibleTramo;
                
                log.debug("Costo combustible tramo {}: ${} ({} L × ${}/L)", 
                    tramo.getId(), costoCombustibleTramo, litrosConsumidos, precioCombustible);
            }
            
            // D. COSTO ESTADÍA EN DEPÓSITO (si aplica)
            if (tramo.involucraEstadiaEnDeposito() && tramo.getCostoEstadia() != null) {
                costoEstadiaTotal += tramo.getCostoEstadia();
                log.debug("Costo estadía tramo {}: ${}", tramo.getId(), tramo.getCostoEstadia());
            }

            long duracionTramoSegundos = calcularDuracionSegundos(
                tramo.getFechaHoraLlegada(),
                tramo.getFechaHoraFin()
            );
            tiempoTotalSegundos += duracionTramoSegundos;
        }
        
        // 5. Calcular total final
        Double costoTotal = costoGestionTotal + costoCamionTotal + 
                           costoCombustibleTotal + costoEstadiaTotal;
        
        log.info("Cálculo completado para ruta ID: {}. Total: ${}", ruta.getId(), costoTotal);
        
        // 6. Retornar DTO con desglose
        return CostoFinalDTO.builder()
            .rutaId(ruta.getId())
            .cantidadTramos(tramos.size())
            .costoGestion(costoGestionTotal)
            .costoCamion(costoCamionTotal)
            .costoCombustible(costoCombustibleTotal)
            .costoEstadia(costoEstadiaTotal)
            .costoTotal(costoTotal)
            .tiempoTotalSegundos(tiempoTotalSegundos)
            .build();
    }

    /**
     * Calcula la duración entre dos fechas en segundos.
     * Si alguna de las fechas es null, devuelve 0.
     *
     * @param fechaInicio fecha inicial.
     * @param fechaFin fecha final.
     * @return duración en segundos entre las dos fechas.
     */
    private long calcularDuracionSegundos(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        long diferenciaMs = fechaFin.getTime() - fechaInicio.getTime();
        return diferenciaMs / 1000; // Convertir a segundos
    }

    
    /**
     * Calcula costos ESTIMADOS para una ruta sin camiones asignados.
     *
     * Este método:
     * 1. Obtiene los tramos de la ruta.
     * 2. Consulta el microservicio de solicitudes para obtener la información del contenedor.
     * 3. Busca camiones compatibles según peso y volumen requeridos.
     * 4. Calcula los promedios de costo y consumo entre los camiones compatibles.
     * 5. Obtiene la tarifa asociada a la ruta.
     * 6. Calcula los costos estimados usando los promedios y los datos de la tarifa.
     *
     * Lanza IllegalStateException si:
     * - La ruta no tiene tramos.
     * - No existen camiones compatibles.
     *
     * @param ruta Ruta para la cual se calcularán los costos estimados.
     * @return objeto DTO con los costos estimados de transporte.
     */
    public CostosEstimadosDTO calcularCostosEstimados(Ruta ruta) {
        
        // 1. Obtener ruta con tramos
        List<Tramo> tramos = tramoService.tramosPorRutaId(ruta.getId());
        if (tramos.isEmpty()) {
            throw new IllegalStateException("La ruta no tiene tramos asignados");
        }
        
        // 2. Obtener información del contenedor de la solicitud llamada a ms solicitudes
        ContenedorResponseDTO contenedor = solicitudesClient.obtenerInfoContenedor(ruta.getSolicitudId());
        
        // 3. Buscar camiones que cumplan con las capacidades
        List<Camion> camionesCompatibles = camionService.findByCapacidades(
            contenedor.peso(), 
            contenedor.volumen()
        );
        
        if (camionesCompatibles.isEmpty()) {
            throw new IllegalStateException("No hay camiones compatibles con las capacidades requeridas");
        }
        
        // 4. Calcular promedios de camiones compatibles
        PromedioCamiones promedio = calcularPromedioCamiones(camionesCompatibles);
        
        // 5. Obtener tarifa
        Tarifa tarifa = ruta.getTarifa();
        Double precioCombustible = tarifa.getPrecioCombustiblePorLitro();
        Double costoGestionPorTramo = tarifa.getCostoGestionPorTramo();
        
        // 6. Calcular costos estimados usando promedios
        return calcularCostosConPromedios(
            ruta.getId(), tramos, promedio, costoGestionPorTramo, precioCombustible, camionesCompatibles.size()
        );
    }
    
    /**
     * Calcula los valores PROMEDIO de los camiones compatibles.
     *
     * Este método:
     * - Calcula el costo por kilómetro promedio entre todos los camiones recibidos.
     * - Calcula el consumo de combustible promedio (litros cada 100 km).
     * - Si no hay valores válidos, devuelve 0.0 como promedio.
     *
     * @param camiones lista de camiones compatibles con la solicitud.
     * @return objeto PromedioCamiones con los promedios calculados.
     */
    private PromedioCamiones calcularPromedioCamiones(List<Camion> camiones) {
        // 1. Calcula el costo por kilómetro promedio entre todos los camiones recibidos. o 0.0 por defecto
        Double costoPorKmPromedio = camiones.stream()
            .mapToDouble(Camion::getCostoPorKm)
            .average()
            .orElse(0.0);
            
        // 2. Calcula el consumo de combustible promedio (litros cada 100 km). o 0.0 por defecto
        Double consumoPromedio = camiones.stream()
            .mapToDouble(Camion::getConsumoCombustibleLx100km)
            .average()
            .orElse(0.0);
            
        return new PromedioCamiones(costoPorKmPromedio, consumoPromedio);
    }
    
    /**
     * Calcula los costos ESTIMADOS de una ruta utilizando valores promedio
     * obtenidos de camiones compatibles.
     *
     * Este método:
     * - Recorre todos los tramos de la ruta.
     * - Suma el costo de gestión por cada tramo.
     * - Calcula el costo estimado del camión en función del costo por km promedio.
     * - Calcula el costo estimado de combustible según el consumo promedio.
     * - Suma costos de estadía si el tramo lo requiere.
     * - Suma el tiempo total estimado de todos los tramos.
     *
     * Finalmente construye y devuelve un objeto CostosEstimadosDTO que agrupa:
     * - Costos desglosados
     * - Costos totales
     * - Promedios utilizados
     * - Tiempo total estimado
     * - Cantidad de camiones compatibles
     *
     * @param rutaId ID de la ruta.
     * @param tramos lista de tramos de la ruta.
     * @param promedio valores promedio de camiones compatibles.
     * @param costoGestionPorTramo costo fijo de gestión por tramo.
     * @param precioCombustible precio del combustible por litro.
     * @param cantidadCamionesCompatibles cantidad de camiones que cumplen las capacidades.
     * @return DTO con todos los costos estimados calculados.
     */
    private CostosEstimadosDTO calcularCostosConPromedios(
        Long rutaId, List<Tramo> tramos, PromedioCamiones promedio,
        Double costoGestionPorTramo, Double precioCombustible, int cantidadCamionesCompatibles) {
        
        // Inicializar acumuladores
        Double costoGestionTotal = 0.0;
        Double distanciaTotal = 0.0;
        Double costoCamionTotal = 0.0;
        Double costoCombustibleTotal = 0.0;
        Double costoEstadiaTotal = 0.0;
        Long tiempoSegundosTotal = (long) 0;
        
        // Procesar cada tramo
        for (Tramo tramo : tramos) {
            // A. CARGO GESTIÓN (siempre se aplica)
            costoGestionTotal += costoGestionPorTramo;
            
            // B. COSTO CAMIÓN ESTIMADO (usando promedio)
            if (tramo.getDistanciaKm() != null) {
                Double costoTramoCamion = tramo.getDistanciaKm() * promedio.costoPorKmPromedio();
                costoCamionTotal += costoTramoCamion;
            }
            
            // C. COSTO COMBUSTIBLE ESTIMADO (usando promedio)
            if (tramo.getDistanciaKm() != null) {
                Double litrosConsumidos = tramo.getDistanciaKm() * (promedio.consumoPromedio() / 100);
                Double costoCombustibleTramo = litrosConsumidos * precioCombustible;
                costoCombustibleTotal += costoCombustibleTramo;
            }
            
            // D. COSTO ESTADÍA (si aplica)
            if (tramo.involucraEstadiaEnDeposito() && tramo.getCostoEstadia() != null) {
                costoEstadiaTotal += tramo.getCostoEstadia();
            }

            // E. acumuladores totales
            if (tramo.getDuracionEstimadaSegundos() != null) {
                tiempoSegundosTotal += tramo.getDuracionEstimadaSegundos();
            }

            if (tramo.getDistanciaKm() != null) {
                distanciaTotal += tramo.getDistanciaKm();
            }
        }
        
        // Calcular total
        Double costoTotal = costoGestionTotal + costoCamionTotal + 
                           costoCombustibleTotal + costoEstadiaTotal;

        // Calcular en horas para mas info
        Double totalHoras = tiempoSegundosTotal / 3600.0;
        
        return CostosEstimadosDTO.builder()
            .rutaId(rutaId)
            .cantidadTramos(tramos.size())
            .cantidadCamionesCompatibles(cantidadCamionesCompatibles)
            .costoGestion(this.round2(costoGestionTotal))
            .costoCamion(this.round2(costoCamionTotal))
            .costoCombustible(this.round2(costoCombustibleTotal))
            .costoEstadia(this.round2(costoEstadiaTotal))

            .consumoPromedio(this.round2(promedio.consumoPromedio()))
            .costoPorKmPromedio(this.round2(promedio.costoPorKmPromedio()))
            .distanciaTotalKm(this.round2(distanciaTotal))
            .costoEstimado(this.round2(costoTotal))
            
            .tiempoEstimadoSegundos(tiempoSegundosTotal)
            .tiempoEstimadoHoras(this.round2(totalHoras))
            .esEstimado(true)
            .fechaCalculo(new Date())
            .build();
    }

    /**
     * Redondea un valor Double a dos decimales usando Math.round().
     */
    private Double round2(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    
    // Records auxiliares
    private record PromedioCamiones(Double costoPorKmPromedio, Double consumoPromedio) {}
}