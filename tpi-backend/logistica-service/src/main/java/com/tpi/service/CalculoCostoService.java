package com.tpi.service;

import com.tpi.model.Tarifa;
import com.tpi.model.Tramo;
import com.tpi.model.Camion;
import com.tpi.model.Ruta;
import com.tpi.client.SolicitudClient;
import com.tpi.dto.CostoFinalDTOs.*;
import com.tpi.dto.external.ContenedorResponseDTO;
import com.tpi.dto.response.CostosEstimadosDTOs.CostosEstimadosDTO;
import com.tpi.dto.response.CostosEstimadosDTOs;

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

        Double distanciaTotalKm = 0.0;
        Long tiempoTotalSegundos = 0L;

        // 4. Procesar cada tramo de la ruta
        for (Tramo tramo : tramos) {

            // A. Gestión
            Double costoGestionTramo = costoGestionPorTramo;
            costoGestionTotal += costoGestionTramo;

            // B. Camión
            Double costoCamionTramo = 0.0;
            if (tramo.getCamion() != null && tramo.getDistanciaKm() != null) {
                costoCamionTramo = tramo.getDistanciaKm() * tramo.getCamion().getCostoPorKm();
                costoCamionTotal += costoCamionTramo;
            }

            // C. Combustible
            Double costoCombustibleTramo = 0.0;
            if (tramo.getCamion() != null && tramo.getDistanciaKm() != null) {
                Double litrosConsumidos = tramo.getDistanciaKm() * 
                                        (tramo.getCamion().getConsumoCombustibleLx100km() / 100);
                costoCombustibleTramo = litrosConsumidos * precioCombustible;
                costoCombustibleTotal += costoCombustibleTramo;
            }

            // D. Estadia
            Double costoEstadiaTramo = 0.0;
            if (tramo.involucraEstadiaEnDeposito() && tramo.getCostoEstadia() != null) {
                costoEstadiaTramo = tramo.getCostoEstadia();
                costoEstadiaTotal += costoEstadiaTramo;
            }

            // E. Tiempo
            long duracionTramoSegundos = calcularDuracionSegundos(
                tramo.getFechaHoraLlegada(),
                tramo.getFechaHoraFin()
            );
            tiempoTotalSegundos += duracionTramoSegundos;

            distanciaTotalKm += tramo.getDistanciaKm();

            // F. Costo real del tramo
            Double costoRealTramo = costoGestionTramo 
                                + costoCamionTramo 
                                + costoCombustibleTramo 
                                + costoEstadiaTramo;

            tramo.setCostoReal(costoRealTramo);
        }

        // 5. Calcular total final
        Double costoTotal = costoGestionTotal + costoCamionTotal 
                        + costoCombustibleTotal + costoEstadiaTotal;

        log.info("Cálculo completado para ruta ID: {}. Total: ${}", ruta.getId(), costoTotal);

        // === 6. Resumen general ===
        ResumenCfDTO resumen = new ResumenCfDTO(
                tramos.size(),
                costoTotal,
                distanciaTotalKm,
                this.round2(tiempoTotalSegundos)
        );

        // === 6.1. Desglose de costos ===
        CostosCfDTO costos = new CostosCfDTO(
                this.round2(costoGestionTotal),
                this.round2(costoCamionTotal),
                this.round2(costoCombustibleTotal),
                this.round2(precioCombustible),
                this.round2(costoEstadiaTotal),
                this.round2( costoTotal)
        );

        // === 6.2. Construir DTO final ===
        return CostoFinalDTO.of(
                ruta,
                resumen,
                costos,
                tramos
        );
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
            ruta.getId(), tramos, promedio, costoGestionPorTramo, precioCombustible, camionesCompatibles
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
     * @param camiones lista de camiones que cumplen las capacidades.
     * @return DTO con todos los costos estimados calculados.
     */
    private CostosEstimadosDTO calcularCostosConPromedios(
        Long rutaId, List<Tramo> tramos, PromedioCamiones promedio,
        Double costoGestionPorTramo, Double precioCombustible, List<Camion> camiones) {
    
        // Acumuladores globales
        double costoGestionTotal = 0.0;
        double distanciaTotal = 0.0;
        double costoCamionTotal = 0.0;
        double costoCombustibleTotal = 0.0;
        double costoEstadiaTotal = 0.0;
        long tiempoSegundosTotal = 0L;

        double costoTotal = 0.0;

        for (Tramo tramo : tramos) {

            // --- COSTOS PARCIALES DEL TRAMO ---
            double subtotalGestion = costoGestionPorTramo;
            double subtotalCamion = 0.0;
            double subtotalCombustible = 0.0;
            double subtotalEstadia = 0.0;

            // A. Coste de camión
            if (tramo.getDistanciaKm() != null) {
                subtotalCamion = tramo.getDistanciaKm() * promedio.costoPorKmPromedio();
            }

            // B. Coste de combustible
            if (tramo.getDistanciaKm() != null) {
                double litrosConsumidos = tramo.getDistanciaKm() * (promedio.consumoPromedio() / 100);
                subtotalCombustible = litrosConsumidos * precioCombustible;
            }

            // C. Estadia si corresponde
            if (tramo.involucraEstadiaEnDeposito() && tramo.getCostoEstadia() != null) {
                subtotalEstadia = tramo.getCostoEstadia();
            }

            // --- SUBTOTAL DEL TRAMO ---
            double subtotalTramo = subtotalGestion + subtotalCamion + subtotalCombustible + subtotalEstadia;

            // Guardar costo aproximado en el tramo
            tramo.setCostoAproximado(this.round2(subtotalTramo));

            // --- SUMAR A LOS ACUMULADORES GLOBALES ---
            costoGestionTotal += subtotalGestion;
            costoCamionTotal += subtotalCamion;
            costoCombustibleTotal += subtotalCombustible;
            costoEstadiaTotal += subtotalEstadia;

            if (tramo.getDistanciaKm() != null) {
                distanciaTotal += tramo.getDistanciaKm();
            }

            if (tramo.getDuracionEstimadaSegundos() != null) {
                tiempoSegundosTotal += tramo.getDuracionEstimadaSegundos();
            }

            costoTotal += subtotalTramo;
        }

        // Horas totales
        double totalHoras = tiempoSegundosTotal / 3600.0;

        // Guardar datos
        tramoService.saveAll(tramos);

        // 1. Construir ResumenDTO
        CostosEstimadosDTOs.ResumenDTO resumen = new CostosEstimadosDTOs.ResumenDTO(
                tramos.size(),                     // cantidadTramos
                camiones.size(),               // cantidadCamionesCompatibles
                round2(costoTotal),                // costoTotal
                round2(distanciaTotal),            // distanciaTotalKm
                round2(totalHoras)                 // tiempoEstimadoHoras
        );

        // 2. Construir CostosDTO
        CostosEstimadosDTOs.CostosDTO costos = new CostosEstimadosDTOs.CostosDTO(
                round2(costoGestionTotal),         // gestion
                round2(costoCamionTotal),          // camion
                round2(costoCombustibleTotal),     // combustible Total
                round2(precioCombustible),     // combustible Litro
                round2(costoEstadiaTotal),         // estadia
                round2(promedio.costoPorKmPromedio()) // costoPorKmPromedio
        );

        // 3. Construir MetricasDTO
        CostosEstimadosDTOs.MetricasDTO metricas = new CostosEstimadosDTOs.MetricasDTO(
                round2(promedio.consumoPromedio()),  // consumoPromedioLx100
                tiempoSegundosTotal                   // tiempoEstimadoSegundos
        );

        // 5. Construir el CostosEstimadosDTO final
        CostosEstimadosDTOs.CostosEstimadosDTO dto = CostosEstimadosDTOs.CostosEstimadosDTO.of(
            rutaId, true, new Date(), resumen, costos, metricas, camiones
        );

        return dto;
    }

    /**
     * Redondea un valor Double a dos decimales usando Math.round().
     */
    private Double round2(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Double round2(Long value) {
        return Math.round(value * 100.0) / 100.0;
    }

    
    // Records auxiliares
    private record PromedioCamiones(Double costoPorKmPromedio, Double consumoPromedio) {}
}