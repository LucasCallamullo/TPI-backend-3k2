package com.tpi.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ActualizarTarifaRequest(
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    String nombre,
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    String descripcion,
    
    @PositiveOrZero(message = "El volumen mínimo debe ser positivo o cero")
    Double volumenMin,
    
    @Positive(message = "El volumen máximo debe ser positivo")
    Double volumenMax,
    
    @Positive(message = "El costo de gestión debe ser positivo")
    Double costoGestionPorTramo,
    
    @Positive(message = "El costo por km debe ser positivo")
    Double costoPorKmBase,
    
    @Positive(message = "El precio del combustible debe ser positivo")
    Double precioCombustiblePorLitro
) {}