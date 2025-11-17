package com.tpi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO para crear una ruta completa asociada a una solicitud")
public class CrearRutaCompletaRequest {
    
    @Schema(
        description = "ID de la solicitud a la que se asociará la ruta",
        example = "123",
        required = true
    )
    @NotNull(message = "El ID de la solicitud es requerido")
    private Long solicitudId;

    @Schema(
        description = "ID de la ubicación de origen",
        example = "456",
        required = true
    )
    @NotNull(message = "El ID de origen es requerido")
    private Long origenId;

    @Schema(
        description = "ID de la ubicación de destino", 
        example = "789",
        required = true
    )
    @NotNull(message = "El ID de destino es requerido")
    private Long destinoId;

    @Schema(
        description = "ID de la tarifa seleccionada",
        example = "1",
        required = true
    )
    @NotNull(message = "El ID de la tarifa es requerido")
    private Long tarifaId;

    @Schema(
        description = "Lista de IDs de depósitos intermedios (puede estar vacía)",
        example = "[101, 205, 308]",
        required = false
    )
    private List<Long> depositosIntermedios;

    // Constructores
    public CrearRutaCompletaRequest() {}

    public CrearRutaCompletaRequest(Long solicitudId, Long origenId, Long destinoId, 
                                   Long tarifaId, List<Long> depositosIntermedios) {
        this.solicitudId = solicitudId;
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.tarifaId = tarifaId;
        this.depositosIntermedios = depositosIntermedios;
    }
}