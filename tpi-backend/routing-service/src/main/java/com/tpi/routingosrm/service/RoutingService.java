package com.tpi.routingosrm.service;

import com.tpi.routingosrm.dto.RouteAlternativeResponse;
import com.tpi.routingosrm.dto.RouteRequest;
import com.tpi.routingosrm.dto.RouteResponse;
import com.tpi.routingosrm.osrm.OsrmResponse;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RoutingService {

    private final WebClient osrmWebClient;

    public RoutingService(WebClient osrmWebClient) {
        this.osrmWebClient = osrmWebClient;
    }

    /**
     * Calcula la ruta principal entre un origen y un destino usando OSRM.
     * Devuelve solo la primera ruta obtenida del servicio.
     *
     * @param req DTO con coordenadas de origen y destino
     * @return RouteResponse con distancia en km, duración en segundos y coordenadas
     */
    public RouteResponse calcularRuta(RouteRequest req) {

        // Construir coordenadas en formato LON,LAT; separadas por ";"
        String coords = req.origenLon() + "," + req.origenLat() + ";" +
                        req.destinoLon() + "," + req.destinoLat();

        // Llamada a OSRM
        OsrmResponse resp = osrmWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/route/v1/driving/{coords}")
                        .queryParam("overview", "false") // no necesitamos geometría detallada
                        .build(coords))
                .retrieve()
                .bodyToMono(OsrmResponse.class)
                .block();

        // Tomar la primera ruta devuelta
        OsrmResponse.Route route = resp.routes().get(0);

        // Convertir a DTO propio
        return new RouteResponse(
                route.distance() / 1000.0,            // pasar metros a km
                Math.round(route.duration()),         // duración en segundos
                req.origenLat(),
                req.origenLon(),
                req.destinoLat(),
                req.destinoLon()
        );
    }


    /**
     * Calcula rutas alternativas entre un origen y un destino usando OSRM.
     * Permite obtener varias rutas distintas usando el parámetro 'alternatives'.
     *
     * @param req DTO con coordenadas de origen y destino
     * @param maxAlternativas número máximo de rutas alternativas a solicitar
     * @return RouteAlternativeResponse con una lista de rutas
     */
    public RouteAlternativeResponse calcularRutasAlternativas(RouteRequest req, int maxAlternativas) {

        // Construir coordenadas en formato LON,LAT
        String coords = req.origenLon() + "," + req.origenLat() + ";" +
                        req.destinoLon() + "," + req.destinoLat();

        // Llamada a OSRM con parámetro alternatives
        OsrmResponse resp = osrmWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/route/v1/driving/{coords}")
                        .queryParam("overview", "false")          // no necesitamos geometría detallada
                        .queryParam("alternatives", maxAlternativas) // pedir rutas alternativas
                        .build(coords))
                .retrieve()
                .bodyToMono(OsrmResponse.class)
                .block();

        // Convertir todas las rutas devueltas a DTOs
        List<RouteResponse> rutas = resp.routes().stream()
                .map(r -> new RouteResponse(
                        r.distance() / 1000.0,          // metros a km
                        Math.round(r.duration()),       // duración en segundos
                        req.origenLat(),
                        req.origenLon(),
                        req.destinoLat(),
                        req.destinoLon()
                ))
                .toList();

        return RouteAlternativeResponse.fromEntity(rutas);
    }

}
