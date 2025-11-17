package com.tpi.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record AsignarRutaRequest(
    @NotNull Long tarifaId,
    List<Long> depositosIntermedios     // Optional - puede ser empty list
) {}
