package com.tpi.client;

import com.tpi.dto.response.SolicitudResponses.ClienteDTO;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.exception.MicroservicioNoDisponibleException;
import com.tpi.service.SecurityContextService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientesServiceClient {
    
    private static final String CLIENTES_PATH = "/api/v1/clientes";

    private final SecurityContextService securityContextService;
    private final RestClient clientesRestClient;

    /**
     * Obitnee una Cliente por el endpotin get id
     */
    public ClienteDTO obtenerClientePorId(String clienteId) {
        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            ClienteDTO response = clientesRestClient
                .get()
                .uri(CLIENTES_PATH + "/" + clienteId)   // GET /api/v1/clientes/{id}
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(ClienteDTO.class);  // ← Devuelve UN solo DTO
                
            return response;
            
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntidadNotFoundException("Cliente", "ID " + clienteId);
            
        } catch (HttpClientErrorException e) {
            throw new MicroservicioNoDisponibleException(
                "clientes-service", 
                "obtener Cliente: " + e.getStatusCode(), 
                e
            );
            
        } catch (Exception e) {
            throw new MicroservicioNoDisponibleException(
                "clientes-service", 
                "obtener Cliente", 
                e
            );
        }
    }
}
