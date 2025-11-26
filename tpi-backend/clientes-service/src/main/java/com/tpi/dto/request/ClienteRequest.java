package com.tpi.dto.request;

public record ClienteRequest(
        String nombre,
        String email,
        String password,
        String telefono,
        String direccion
) {}

