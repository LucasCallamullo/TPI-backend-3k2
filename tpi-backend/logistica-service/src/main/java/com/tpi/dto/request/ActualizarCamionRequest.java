package com.tpi.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ActualizarCamionRequest(
    @Pattern(regexp = "^[A-Z0-9]{6,7}$", message = "Formato de dominio inválido")
    String dominio,
    
    @Size(max = 100, message = "Nombre demasiado largo")
    String nombreConductor,
    
    @Pattern(regexp = "^\\+?[0-9\\s-]{10,}$", message = "Formato de teléfono inválido")
    String telefonoConductor,
    
    Boolean disponible,
    
    @Positive(message = "El costo por km debe ser positivo")
    Double costoPorKm,
    
    @Positive(message = "El consumo debe ser positivo")
    Double consumoCombustibleLx100km,
    
    @Size(max = 50, message = "Modelo demasiado largo")
    String modelo,
    
    @Positive(message = "La capacidad de peso debe ser positiva")
    Double capacidadPesoKg,
    
    @Positive(message = "La capacidad de volumen debe ser positiva")
    Double capacidadVolumenM3
) {}