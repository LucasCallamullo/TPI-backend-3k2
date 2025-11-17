package com.tpi.client;

import com.tpi.dto.response.RouteResponse;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.exception.MicroservicioNoDisponibleException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class RoutingClient {
    
    // URL para OBTENER contenedor (no crear ubicación)
    // private static final String CONTENEDORES_PATH = "/api/v1/contenedores"; //  Solo el path

    // private final SecurityContextService securityContextService;
    private final RestClient routingRestClient;     // Nombre coincide con bean en RestConfig

    public RouteResponse calcularRutaCompleta(double origenLat, double origenLon, 
                                         double destinoLat, double destinoLon) {
        try {
            RouteResponse response = routingRestClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/api/v1/routing/calcular-ruta")
                    .queryParam("origenLat", origenLat)
                    .queryParam("origenLon", origenLon)
                    .queryParam("destinoLat", destinoLat)
                    .queryParam("destinoLon", destinoLon)
                    .build())
                .retrieve()
                .body(RouteResponse.class);
            
            if (response == null) {
                throw new MicroservicioNoDisponibleException(
                    "servicio de routing", "Respuesta vacía", null
                );
            }
            
            return response;
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EntidadNotFoundException("Ruta", "coordenadas proporcionadas");
            }
            throw new MicroservicioNoDisponibleException("Servicio de routing no disponible", e.getMessage(), e);
        } catch (Exception e) {
            throw new MicroservicioNoDisponibleException("Error al calcular ruta: ", e.getMessage(), e);
        }
    }
}