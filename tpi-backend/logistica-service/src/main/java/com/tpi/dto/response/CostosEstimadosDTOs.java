package com.tpi.dto.response;

import java.util.Date;
import java.util.List;
import com.tpi.model.Camion;

public class CostosEstimadosDTOs {

    public record CostosEstimadosDTO(
        Long rutaId,
        boolean esEstimado,
        Date fechaCalculo,

        ResumenDTO resumen,
        CostosDTO costos,
        MetricasDTO metricas,

        // Opcionales
        List<CamionCostosDTO> camionesCompatibles
    ) {
        public static CostosEstimadosDTO of(Long rutaId, boolean esEstimado, Date fechaCalculo, ResumenDTO resumen,
            CostosDTO costos, MetricasDTO metricas, List<Camion> camiones) {


            // Convertir camiones -> CamionDTO
            List<CamionCostosDTO> camionesDto = camiones != null
                    ? camiones.stream()
                            .map(CamionCostosDTO::fromEntity)
                            .toList()
                    : null;
            
            return new CostosEstimadosDTO(
                rutaId, esEstimado, fechaCalculo, resumen, costos, metricas, camionesDto
            );
        }
    
    }

    public record ResumenDTO(
        Integer cantidadTramos,
        Integer cantidadCamionesCompatibles,
        Double costoTotal,
        Double distanciaTotalKm,
        Double tiempoEstimadoHoras
    ) {}

    public record CostosDTO(
        Double gestion,
        Double camion,
        Double combustibleTotal,
        Double combustibleLitro,
        Double estadia,
        Double costoPorKmPromedio
    ) {}

    public record MetricasDTO(
        Double consumoPromedioLx100,
        Long tiempoEstimadoSegundos
    ) {}

    public record CamionCostosDTO(
        Long id,
        String dominio,
        Boolean disponible,
        Double costoPorKm,
        Double consumoLx100
    ) {
        public static CamionCostosDTO fromEntity(Camion camion) { 
            
            return new CamionCostosDTO(
                camion.getId(), 
                camion.getDominio(), 
                camion.getDisponible(), 
                camion.getCostoPorKm(), 
                camion.getConsumoCombustibleLx100km()
            );
        }
    }
}