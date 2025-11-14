package com.tpi.dto.request;

public record CrearSolicitudCompletaRequestDTO(
    ContenedorRequestDTO contenedor,
    UbicacionRequestDTO origen,
    UbicacionRequestDTO destino
) {}