package com.tpi.dto.external; // O el package que uses para DTOs externos

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DEBE coincidir exactamente con el del MS-Solicitudes
public record SolicitudResponseDTO(
    Long id,
    String estado,
    ContenedorResponseDTO contenedor, // Mismo nombre
    Long origenId,
    Long destinoId,
    BigDecimal costoEstimado,
    Integer tiempoEstimado,
    BigDecimal costoFinal,
    Integer tiempoReal,
    LocalDateTime fechaCreacion
) {
    // Constructor vacío para Jackson
    public SolicitudResponseDTO() {
        this(null, null, null, null, null, null, null, null, null, null);
    }
}