package com.tpi.dto.request;

import java.util.List;

public record AsignarRutaRequest(
    List<Long> depositosIntermedios     // Optional - puede ser empty list
) {}
