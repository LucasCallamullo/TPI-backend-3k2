package com.tpi.dto.response;

import com.tpi.model.Contenedor;

public record ContenedorResponseDTO(
    Long id,
    Double peso,
    Double volumen,
    String identificacionUnica,
    EstadoContenedorInfoDTO estado 
) {
    // Constructor cuando ya tienes el estado cargado (EAGER o JOIN)
    public static ContenedorResponseDTO fromEntity(Contenedor contenedor) {
        if (contenedor == null) {
            return null;
        }
        return new ContenedorResponseDTO(
            contenedor.getId(),
            contenedor.getPeso(),
            contenedor.getVolumen(),
            contenedor.getIdentificacionUnica(),
            new EstadoContenedorInfoDTO(
                contenedor.getEstado().getId(),
                contenedor.getEstado().getNombre()
            )
        );
    }

    // Constructor cuando pasas el estado explícitamente (para casos como actualizarEstado)
    public static ContenedorResponseDTO fromEntity(Contenedor contenedor, EstadoContenedorInfoDTO estado) {
        if (contenedor == null) {
            return null;
        }
        return new ContenedorResponseDTO(
            contenedor.getId(),
            contenedor.getPeso(),
            contenedor.getVolumen(),
            contenedor.getIdentificacionUnica(),
            estado
        );
    }
}