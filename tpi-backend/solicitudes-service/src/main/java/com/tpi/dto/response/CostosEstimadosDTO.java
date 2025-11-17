package com.tpi.dto.response;


public record CostosEstimadosDTO(
    Long rutaId,
    Double costoEstimado,
    Double tiempoEstimado
) {}