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

// import com.tpi.model.Contenedor;

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

    private long calcularDuracionSegundos(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        long diferenciaMs = fechaFin.getTime() - fechaInicio.getTime();
        return diferenciaMs / 1000; // Convertir a segundos
    }
    
    /**
     * Calcula costos ESTIMADOS para una ruta (sin camiones asignados)
     * Busca camiones que cumplan con las capacidades requeridas y promedia costos
     */
    @SuppressWarnings("null")
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
     * Calcula promedios de los camiones disponibles que cumplan con la capacidad para el contenedor
     */
    private PromedioCamiones calcularPromedioCamiones(List<Camion> camiones) {
        Double costoPorKmPromedio = camiones.stream()
            .mapToDouble(Camion::getCostoPorKm)
            .average()
            .orElse(0.0);
            
        Double consumoPromedio = camiones.stream()
            .mapToDouble(Camion::getConsumoCombustibleLx100km)
            .average()
            .orElse(0.0);
            
        return new PromedioCamiones(costoPorKmPromedio, consumoPromedio);
    }
    
    private CostosEstimadosDTO calcularCostosConPromedios(
        Long rutaId, List<Tramo> tramos, PromedioCamiones promedio,
        Double costoGestionPorTramo, Double precioCombustible, int cantidadCamionesCompatibles) {
        
        // Inicializar acumuladores
        Double costoGestionTotal = 0.0;
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

            tiempoSegundosTotal += tramo.getDuracionEstimadaSegundos();
        }
        
        // Calcular total
        Double costoTotal = costoGestionTotal + costoCamionTotal + 
                           costoCombustibleTotal + costoEstadiaTotal;
        
        return CostosEstimadosDTO.builder()
            .rutaId(rutaId)
            .cantidadTramos(tramos.size())
            .cantidadCamionesCompatibles(cantidadCamionesCompatibles)
            .costoGestion(costoGestionTotal)
            .costoCamion(costoCamionTotal)
            .costoCombustible(costoCombustibleTotal)
            .costoEstadia(costoEstadiaTotal)
            .costoTotal(costoTotal)
            .costoPorKmPromedio(promedio.costoPorKmPromedio())
            .consumoPromedio(promedio.consumoPromedio())
            .tiempoEstimadoSegundos(tiempoSegundosTotal)
            .esEstimado(true)
            .fechaCalculo(new Date())
            .build();
    }
    
    // Records auxiliares
    private record PromedioCamiones(Double costoPorKmPromedio, Double consumoPromedio) {}
}