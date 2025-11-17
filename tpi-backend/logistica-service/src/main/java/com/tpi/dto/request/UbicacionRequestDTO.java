package com.tpi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para creación de ubicaciones")
public record UbicacionRequestDTO(
    
    @Schema(
        description = "Dirección completa de la ubicación",
        example = "Av. Corrientes 1234, Microcentro",
        required = true
    )
    @NotBlank(message = "La dirección es requerida")
    String direccion,

    @Schema(
        description = "Nombre descriptivo de la ubicación", 
        example = "Depósito Central Buenos Aires",
        required = true
    )
    @NotBlank(message = "El nombre es requerido")
    String nombre,

    @Schema(
        description = "Coordenada de latitud geográfica",
        example = "-34.603722",
        required = true
    )
    @NotNull(message = "La latitud es requerida")
    Double latitud,

    @Schema(
        description = "Coordenada de longitud geográfica",
        example = "-58.381592", 
        required = true
    )
    @NotNull(message = "La longitud es requerida")
    Double longitud,

    @Schema(
        description = "ID del tipo de ubicación",
        example = "1",
        required = true
    )
    @NotNull(message = "El tipo de ubicación es requerido")
    Long tipoId  // ← Recibir el ID, no el objeto completo
) {}
