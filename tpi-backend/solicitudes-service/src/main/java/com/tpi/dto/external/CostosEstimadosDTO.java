package com.tpi.dto.external;

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
    private Double costoTotal;
    private Double costoPorKmPromedio;
    private Double consumoPromedio;
    private Boolean esEstimado;
    private Date fechaCalculo;
    private Long tiempoEstimadoSegundos;
}
