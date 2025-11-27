package com.tpi.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;

public record SolicitudUpdateEstadoResponseDTO(
    @Schema(description = "ID único de la solicitud", example = "1")
    Long id,
    
    @Schema(
        description = "Nuevo estado de la solicitud",
        example = "PROGRAMADA"
    )
    String estado,
    
    @Schema(
        description = "Mensaje descriptivo de la operación",
        example = "Solicitud programada exitosamente"
    )
    String mensaje
) {}
