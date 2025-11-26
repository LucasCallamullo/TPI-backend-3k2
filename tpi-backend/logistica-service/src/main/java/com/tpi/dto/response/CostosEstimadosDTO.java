package com.tpi.dto.response;

import lombok.Data;
import lombok.Builder;

import java.util.Date;

@Data
@Builder
public class CostosEstimadosDTO {
    private Long rutaId;
    private Integer cantidadTramos;
    private Integer cantidadCamionesCompatibles;

    private Double costoGestion;
    private Double costoCamion;
    private Double costoCombustible;
    private Double costoEstadia;

    private Double consumoPromedio;
    private Double costoPorKmPromedio;
    private Double distanciaTotalKm;

    private Long tiempoEstimadoSegundos;
    private Double tiempoEstimadoHoras;

    private Double costoEstimado;

    
    private Boolean esEstimado;
    private Date fechaCalculo;
}