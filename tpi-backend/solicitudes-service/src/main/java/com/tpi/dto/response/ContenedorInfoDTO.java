package com.tpi.dto.response;

import com.tpi.model.Contenedor;

public record ContenedorInfoDTO(
    String identificacionUnica, 
    String estado
) {
    // Constructor estático - la forma correcta
    public static ContenedorInfoDTO fromEntity(Contenedor contenedor) {
        return new ContenedorInfoDTO(
            contenedor.getIdentificacionUnica(),
            contenedor.getEstado().getNombre()
        );
    }
}