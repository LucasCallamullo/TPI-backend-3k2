package com.tpi.dto.external;

// DEBE coincidir exactamente con el del MS-Solicitudes
public record ContenedorResponseDTO(
    Long id,
    Double peso,           // ← peso (no pesoMaximoKg)
    Double volumen,        // ← volumen (no volumenMaximoM3)
    String identificacionUnica,
    EstadoContenedorInfoDTO estado
) {
    public ContenedorResponseDTO() {
        this(null, null, null, null, null);
    }
}