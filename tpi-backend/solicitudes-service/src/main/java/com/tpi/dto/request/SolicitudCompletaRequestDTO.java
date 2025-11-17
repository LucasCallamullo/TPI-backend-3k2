package com.tpi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/*
 * 
 * Ejemplo de uso
{
  "contenedor": {
    "identificacionUnica": "CONT-2025-001-ABC123",
    "peso": 1500.5,
    "volumen": 33.2
  },
  "origen": {
    "direccion": "Av. Corrientes 1234, Microcentro, Buenos Aires",
    "nombre": "Depósito Central Buenos Aires", 
    "latitud": -34.603722,
    "longitud": -58.381592,
    "tipoId": 1
  },
  "destino": {
    "direccion": "Ruta 8 Km 45, Pilar, Buenos Aires",
    "nombre": "Terreno Cliente - Obra Norte",
    "latitud": -34.473890,
    "longitud": -58.913610,
    "tipoId": 4
  }
}
 */
@Schema(description = "DTO para creación completa de una solicitud de transporte")
public record SolicitudCompletaRequestDTO(
    
    @Schema(
        description = "Datos del contenedor a transportar",
        required = true
    )
    ContenedorRequestDTO contenedor,
    
    @Schema(
        description = "Ubicación de origen del transporte",
        required = true
    )
    UbicacionRequestDTO origen,
    
    @Schema(
        description = "Ubicación de destino del transporte", 
        required = true
    )
    UbicacionRequestDTO destino
) {}