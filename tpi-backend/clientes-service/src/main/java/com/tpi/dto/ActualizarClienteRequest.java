package com.tpi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "ActualizarClienteRequest",
    description = "DTO para actualización parcial de datos del cliente"
)
public record ActualizarClienteRequest(
    @Schema(
        description = "Nombre completo del cliente",
        example = "Juan Carlos Pérez",
        nullable = true
    )
    String nombre,
    
    @Schema(
        description = "Dirección de email", 
        example = "juan.perez@email.com",
        nullable = true
    )
    String email,
    
    @Schema(
        description = "Número de teléfono",
        example = "+54 11 1234-5678", 
        nullable = true
    )
    String telefono,
    
    @Schema(
        description = "Dirección física",
        example = "Av. Siempre Viva 123, Springfield",
        nullable = true 
    )
    String direccion
) {}