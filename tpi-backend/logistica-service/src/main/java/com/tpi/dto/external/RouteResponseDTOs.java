package com.tpi.dto.external;

import java.util.List;

public class RouteResponseDTOs {

    public record RouteAlternativeResponse(
        Integer bestRuta,               // Índice o ID de la mejor ruta
        List<RouteResponse> rutas
    ) {}

    // En el microservicio routing - CAMBIAR a:
    public record RouteResponse(
        Double distanciaKm,        // Cambiar a Double
        Long duracionSegundos,     // Cambiar a Long  
        Double origenLat,
        Double origenLon,
        Double destinoLat,
        Double destinoLon
    ) {}
    
}
