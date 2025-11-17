package com.tpi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteRequest {
    private double origenLat;
    private double origenLon;
    private double destinoLat;
    private double destinoLon;
}