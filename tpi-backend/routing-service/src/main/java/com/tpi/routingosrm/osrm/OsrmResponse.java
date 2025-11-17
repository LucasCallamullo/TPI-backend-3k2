package com.tpi.routingosrm.osrm;

import java.util.List;

public record OsrmResponse(
        List<Route> routes,
        String code
) {
    public record Route(
            double distance,
            double duration
    ) {}
}
