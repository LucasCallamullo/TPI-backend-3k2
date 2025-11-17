package com.tpi.routingosrm.controller;

import com.tpi.routingosrm.dto.RouteRequest;
import com.tpi.routingosrm.dto.RouteResponse;
import com.tpi.routingosrm.service.RoutingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routing")
public class RoutingController {

    private final RoutingService service;

    public RoutingController(RoutingService service) {
        this.service = service;
    }

    @GetMapping("/calcular-ruta")
    public RouteResponse calcular(
            @RequestParam double origenLat,
            @RequestParam double origenLon,
            @RequestParam double destinoLat,
            @RequestParam double destinoLon
    ) {
        RouteRequest req = new RouteRequest(origenLat, origenLon, destinoLat, destinoLon);
        return service.calcularRuta(req);
    }
}
