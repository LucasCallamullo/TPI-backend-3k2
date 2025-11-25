package com.tpi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload para registrar una nueva tarifa")
public class TarifaRequest {

    @Schema(
        description = "Nombre descriptivo de la tarifa. Ejemplo: 'Tarifa Pequeño - 0-20m³'",
        example = "Tarifa Pequeño - 0-20m³"
    )
    private String nombre;

    @Schema(
        description = "Descripción detallada de la tarifa",
        example = "Para contenedores pequeños de hasta 20m³. Ideal para viviendas básicas."
    )
    private String descripcion;

    @Schema(
        description = "Volumen mínimo del contenedor en m³",
        example = "0"
    )
    private Double volumenMin;

    @Schema(
        description = "Volumen máximo del contenedor en m³",
        example = "20"
    )
    private Double volumenMax;

    @Schema(
        description = "Costo fijo por tramo del viaje",
        example = "1500.0"
    )
    private Double costoGestionPorTramo;

    @Schema(
        description = "Precio estimado por litro de combustible considerado en la tarifa",
        example = "950.0"
    )
    private Double precioCombustiblePorLitro;
}


