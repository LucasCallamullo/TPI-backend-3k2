package com.tpi.dto.request;

public record UbicacionRequestDTO(
    String direccion,
    String nombre,
    Double latitud,
    Double longitud,
    String tipo
) {}