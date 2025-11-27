package com.tpi.dto.external;

import lombok.Data;
import lombok.Builder;
import java.util.Date;
import java.util.List;

@Data
@Builder
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
    ) {}
    
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
    ) {}
}
