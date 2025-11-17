package com.tpi.dto.response;

// En el microservicio routing - CAMBIAR a:
public record RouteResponse(
    Double distanciaKm,        // Cambiar a Double
    Long duracionSegundos,     // Cambiar a Long  
    Double origenLat,
    Double origenLon,
    Double destinoLat,
    Double destinoLon
) {}