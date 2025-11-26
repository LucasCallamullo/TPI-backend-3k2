package com.tpi.dto.external;

public record ClienteRequestDTO(
        String nombre,
        String email,
        String password,
        String telefono,
        String direccion
) {}

