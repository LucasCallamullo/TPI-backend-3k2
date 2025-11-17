package com.tpi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CostoFinalDTO {
    private Long rutaId;
    private Integer cantidadTramos;
    
    // DESGLOSE DE COSTOS
    private Double costoGestion;
    private Double costoCamion;
    private Double costoCombustible;
    private Double costoEstadia;
    private Double costoTotal;
    private Long tiempoTotalSegundos;
    
    // Método helper para formatear
    public String getResumen() {
        return String.format(
            "Ruta %d - %d tramos: Gestión: $%.2f, Camión: $%.2f, Combustible: $%.2f, Estadía: $%.2f, TOTAL: $%.2f",
            rutaId, cantidadTramos, costoGestion, costoCamion, costoCombustible, costoEstadia, costoTotal
        );
    }
}
