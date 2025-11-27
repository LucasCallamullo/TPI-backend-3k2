package com.tpi.routingosrm.dto;

import java.util.List;

public record RouteAlternativeResponse(
    Integer bestRuta,               // Índice o ID de la mejor ruta
    List<RouteResponse> rutas
) {
    
    /**
     * Crea el DTO de rutas alternativas y calcula la mejor opción.
     * Actualmente la mejor ruta se define como la de menor duración.
     *
     * @param rutas Lista de rutas alternativas
     * @return RouteAlternativeResponse con la mejor ruta identificada
     */
    public static RouteAlternativeResponse fromEntity(List<RouteResponse> rutas) {
        if (rutas == null || rutas.isEmpty()) {
            return new RouteAlternativeResponse(null, rutas);
        }

        // Encontrar la ruta con menor duración
        RouteResponse mejorRuta = rutas.stream()
                .min((r1, r2) -> Long.compare(r1.duracionSegundos(), r2.duracionSegundos()))
                .orElse(rutas.get(0));

        // Usamos el índice como identificador simple
        int bestIndex = rutas.indexOf(mejorRuta);

        return new RouteAlternativeResponse(bestIndex, rutas);
    }
}


