package com.tpi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para datos de ubicación geográfica")
public record UbicacionRequestDTO(
    
    @Schema(
        description = "Dirección completa de la ubicación",
        example = "Av. Corrientes 1234, Microcentro",
        required = true,
        maxLength = 255
    )
    @NotBlank(message = "La dirección es requerida")
    String direccion,

    @Schema(
        description = "Nombre descriptivo de la ubicación",
        example = "Depósito Central Buenos Aires",
        required = true,
        maxLength = 100
    )
    @NotBlank(message = "El nombre es requerido")
    String nombre,

    @Schema(
        description = "Coordenada de latitud geográfica",
        example = "-34.603722",
        required = true,
        minimum = "-90.0",
        maximum = "90.0"
    )
    @NotNull(message = "La latitud es requerida")
    Double latitud,

    @Schema(
        description = "Coordenada de longitud geográfica", 
        example = "-58.381592",
        required = true,
        minimum = "-180.0",
        maximum = "180.0"
    )
    @NotNull(message = "La longitud es requerida")
    Double longitud,

    /**
     * A lo sumo aca de distinto es mandar un id, y despues hace la logica ubicacion para asociarlo
     * 
     */
    @Schema(
        description = "ID del tipo de ubicación. Valores: 1=Depósito, 2=Puerto, 3=Aeropuerto, 4=Cliente",
        example = "1",
        required = true
    )
    @NotNull(message = "El ID del tipo de ubicación es requerido")
    Long tipoId  
) {}