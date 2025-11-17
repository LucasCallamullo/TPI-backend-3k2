package com.tpi.routingosrm.dto;

public record RouteRequest(
        double origenLat,
        double origenLon,
        double destinoLat,
        double destinoLon
) {}
