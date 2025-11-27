package com.tpi.client;

import com.tpi.dto.external.RouteResponseDTOs.RouteAlternativeResponse;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.exception.MicroservicioNoDisponibleException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class RoutingClient {

    // Injects the RestClient bean named "routingRestClient" created in RestConfig.
    // This client handles communication with the routing microservice.
    private final RestClient routingRestClient;

    /**
     * Calls the routing microservice to calculate a complete route between two coordinates.
     *
     * @param origenLat   latitude of the origin point
     * @param origenLon   longitude of the origin point
     * @param destinoLat  latitude of the destination point
     * @param destinoLon  longitude of the destination point
     * @return            RouteResponse object containing distance, duration and steps
     */
    public RouteAlternativeResponse calcularRutaCompleta(double origenLat, double origenLon, 
                                              double destinoLat, double destinoLon) {
        try {
            // Builds and executes a GET request to the routing endpoint.
            RouteAlternativeResponse response = routingRestClient.get()
                .uri(uriBuilder -> uriBuilder
                    // .path("/api/v1/routing/calcular-ruta")   // Endpoint of the routing microservice
                    .path("/api/v1/routing/calcular-rutas-alternativas")   // Endpoint of the routing microservice
                    .queryParam("origenLat", origenLat)      // Adds query parameters for origin/destination
                    .queryParam("origenLon", origenLon)
                    .queryParam("destinoLat", destinoLat)
                    .queryParam("destinoLon", destinoLon)
                    .build())
                .retrieve()                                  // Sends the request and retrieves the response
                .body(RouteAlternativeResponse.class);                  // Converts JSON into a RouteResponse object
            
            // Checks if the microservice returned no content (unexpected)
            if (response == null) {
                throw new MicroservicioNoDisponibleException(
                    "servicio de routing", "Respuesta vacía", null
                );
            }
            
            return response;  // Returns the valid response
            
        } catch (HttpClientErrorException e) {
            // Specific handling for 404 errors coming from the routing service
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EntidadNotFoundException(
                    "Ruta", "coordenadas proporcionadas");
            }
            // For any other 4xx errors coming from the routing service
            throw new MicroservicioNoDisponibleException(
                "Servicio de routing no disponible", e.getMessage(), e);
        
        } catch (Exception e) {
            // General exception for network errors, timeouts, parsing errors, etc.
            throw new MicroservicioNoDisponibleException(
                "Error al calcular ruta: ", e.getMessage(), e);
        }
    }
}
