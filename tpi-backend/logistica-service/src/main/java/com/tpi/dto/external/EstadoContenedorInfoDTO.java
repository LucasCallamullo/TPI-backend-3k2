package com.tpi.dto.external;

public record EstadoContenedorInfoDTO(
    Long id,
    String nombre
) {
    public EstadoContenedorInfoDTO() {
        this(null, null);
    }
}