package com.tpi.routingosrm.service;

import com.tpi.routingosrm.dto.RouteRequest;
import com.tpi.routingosrm.dto.RouteResponse;
import com.tpi.routingosrm.osrm.OsrmResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RoutingService {

    private final WebClient osrmWebClient;

    public RoutingService(WebClient osrmWebClient) {
        this.osrmWebClient = osrmWebClient;
    }

    public RouteResponse calcularRuta(RouteRequest req) {

        // OSRM necesita LON,LAT
        String coords = req.origenLon() + "," + req.origenLat() + ";" +
                        req.destinoLon() + "," + req.destinoLat();

        OsrmResponse resp = osrmWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/route/v1/driving/{coords}")
                        .queryParam("overview", "false")
                        .build(coords))
                .retrieve()
                .bodyToMono(OsrmResponse.class)
                .block();

        OsrmResponse.Route route = resp.routes().get(0);

        return new RouteResponse(
                route.distance() / 1000.0,
                Math.round(route.duration()),
                req.origenLat(),
                req.origenLon(),
                req.destinoLat(),
                req.destinoLon()
        );
    }
}
