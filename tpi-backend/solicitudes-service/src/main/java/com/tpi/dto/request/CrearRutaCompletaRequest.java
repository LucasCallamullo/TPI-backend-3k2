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
        example = "123"
    )
    @NotNull(message = "El ID de la solicitud es requerido")
    private Long solicitudId;

    @Schema(
        description = "ID de la ubicación de origen",
        example = "456"
    )
    @NotNull(message = "El ID de origen es requerido")
    private Long origenId;

    @Schema(
        description = "ID de la ubicación de destino", 
        example = "789"
    )
    @NotNull(message = "El ID de destino es requerido")
    private Long destinoId;

    @Schema(
        description = "Volumen del contenedor asociado a la solicitud necesario para asignar la tarifa correspondiente",
        example = "50.5"
    )
    @NotNull(message = "El Volumen del contenedor asociado es requerido")
    private Double volumenContenedor;

    @Schema(
        description = "Lista de IDs de depósitos intermedios (puede estar vacía)",
        example = "[101, 205, 308]"
    )
    private List<Long> depositosIntermedios;

    // Constructores
    public CrearRutaCompletaRequest() {}

    public CrearRutaCompletaRequest(
        Long solicitudId, Double volumen, Long origenId, Long destinoId, List<Long> depositosIntermedios) {
            
        this.solicitudId = solicitudId;
        this.volumenContenedor = volumen;
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.depositosIntermedios = depositosIntermedios;
    }
}