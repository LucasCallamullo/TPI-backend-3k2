package com.tpi.dto.response;

import com.tpi.model.Contenedor;

public record ContenedorResponseDTO(
    Long id,
    Double peso,
    Double volumen,
    String identificacionUnica,
    String estado 
) {
    // Constructor estático en el DTO
    public static ContenedorResponseDTO fromEntity(Contenedor contenedor) {
        if (contenedor == null) {
            return null;
        }
        return new ContenedorResponseDTO(
            contenedor.getId(),
            contenedor.getPeso(),
            contenedor.getVolumen(),
            contenedor.getIdentificacionUnica(),
            contenedor.getEstado() != null ? contenedor.getEstado().getNombre() : "SIN_ESTADO"
        );
    }
}