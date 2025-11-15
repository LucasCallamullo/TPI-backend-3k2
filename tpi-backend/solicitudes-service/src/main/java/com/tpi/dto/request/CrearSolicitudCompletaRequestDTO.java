package com.tpi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para creación completa de una solicitud de transporte")
public record CrearSolicitudCompletaRequestDTO(
    
    @Schema(
        description = "Datos del contenedor a transportar",
        required = true
    )
    ContenedorRequestDTO contenedor,
    
    @Schema(
        description = "Ubicación de origen del transporte",
        required = true
    )
    UbicacionRequestDTO origen,
    
    @Schema(
        description = "Ubicación de destino del transporte", 
        required = true
    )
    UbicacionRequestDTO destino
) {}