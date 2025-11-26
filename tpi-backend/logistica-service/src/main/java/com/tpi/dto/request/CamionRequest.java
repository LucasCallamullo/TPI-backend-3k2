package com.tpi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload para crear un nuevo camión en el sistema")
public record CamionRequest(

    @Schema(
        description = "Dominio o patente única del camión",
        example = "AB123CD",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String dominio,

    @Schema(
        description = "Nombre del conductor asignado al camión",
        example = "Juan Pérez"
    )
    String nombreConductor,

    @Schema(
        description = "Teléfono de contacto del conductor",
        example = "+54 9 11 1234 5678"
    )
    String telefonoConductor,

    @Schema(
        description = "Indica si el camión está disponible para operaciones",
        example = "true",
        defaultValue = "true"
    )
    Boolean disponible,

    @Schema(
        description = "Costo por kilómetro recorrido",
        example = "150.0"
    )
    Double costoPorKm,

    @Schema(
        description = "Consumo de combustible en litros cada 100 km",
        example = "25.5"
    )
    Double consumoCombustibleLx100km,

    @Schema(
        description = "Modelo del camión",
        example = "Scania R500"
    )
    String modelo,

    @Schema(
        description = "Capacidad máxima de carga en kilogramos",
        example = "12000.0"
    )
    Double capacidadPesoKg,

    @Schema(
        description = "Capacidad máxima de volumen en metros cúbicos",
        example = "50.0"
    )
    Double capacidadVolumenM3
) {}
