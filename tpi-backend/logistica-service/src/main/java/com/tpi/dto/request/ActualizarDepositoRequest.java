package com.tpi.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ActualizarDepositoRequest(
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    String nombre,
    
    @Positive(message = "El costo de estadía debe ser positivo")
    Double costoEstadiaPorDia,
    
    Long ubicacionId
) {}
