package com.tpi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO para creación de contenedores")
public record ContenedorRequestDTO(
    
    @Schema(
        description = "Peso del contenedor en kilogramos",
        example = "1500.5",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "0.1"
    )
    @NotNull(message = "El peso es requerido")
    @Positive(message = "El peso debe ser mayor a 0")
    Double peso,
    
    @Schema(
        description = "Volumen del contenedor en metros cúbicos", 
        example = "33.2",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "0.1"
    )
    @NotNull(message = "El volumen es requerido")
    @Positive(message = "El volumen debe ser mayor a 0")
    Double volumen,
    
    @Schema(
        description = "Identificación única del contenedor",
        example = "CONT-2024-001-ABC123",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50
    )
    @NotBlank(message = "La identificación única es requerida")
    String identificacionUnica
) {}