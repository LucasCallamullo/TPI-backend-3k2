package com.tpi.dto.request;

import com.tpi.dto.external.ClienteRequestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class SolicitudesRequestDTOs {

    @Schema(description = "DTO para creación completa de una solicitud de transporte")
    public record SolicitudCompletaRequestDTO(
        
        @Schema(
            description = "Datos del cliente de la solicitud",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        ClienteRequestDTO cliente,    // nuevo

        @Schema(
            description = "Datos del contenedor a transportar",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Valid
        @NotNull(message = "Los datos del contenedor son obligatorios")
        ContenedorRequestDTO contenedor,
        
        @Schema(
            description = "Ubicación de origen del transporte",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Valid
        @NotNull(message = "La ubicación de origen es obligatoria")
        UbicacionRequestDTO origen,
        
        @Schema(
            description = "Ubicación de destino del transporte", 
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Valid
        @NotNull(message = "La ubicación de destino es obligatoria")
        UbicacionRequestDTO destino
    ) {}


    @Schema(description = "DTO para creación completa de una solicitud de transporte")
    public record SolicitudClienteRequestDTO(
        
        @Schema(
            description = "Id del contenedor a transportar",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Valid
        @NotNull(message = "El Id del contenedor es obligatorios")
        Long contenedorId,
        
        @Schema(
            description = "Ubicación de origen del transporte",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Valid
        @NotNull(message = "La ubicación de origen es obligatoria")
        UbicacionRequestDTO origen,
        
        @Schema(
            description = "Ubicación de destino del transporte", 
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Valid
        @NotNull(message = "La ubicación de destino es obligatoria")
        UbicacionRequestDTO destino
    ) {}
}



