package com.tpi.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para respuesta de ubicaciones")
// UbicacionResponses.java
public class UbicacionResponses {
    
    public record UbicacionResponseDTO(
        Long id,
        String direccion,
        String nombre,
        Double latitud,
        Double longitud,
        TipoUbicacionInfoDTO tipo
    ) {}
    
    public record TipoUbicacionInfoDTO(
        Long id,
        String nombre
    ) {}
}