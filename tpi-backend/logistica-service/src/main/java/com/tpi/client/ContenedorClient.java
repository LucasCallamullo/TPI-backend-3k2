package com.tpi.client;

import com.tpi.dto.ContenedorDTO;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.exception.MicroservicioNoDisponibleException;
import com.tpi.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
// import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ContenedorClient {
    
    // URL para OBTENER contenedor (no crear ubicación)
    private static final String CONTENEDORES_PATH = "/api/v1/contenedores"; //  Solo el path

    private final SecurityContextService securityContextService;
    private final RestClient contenedorRestClient;     // Nombre coincide con bean en RestConfig

    public ContenedorDTO obtenerContenedor(Long contenedorId) {
        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            // GET para obtener contenedor por ID
            ContenedorDTO contenedor = contenedorRestClient
                .get()
                // URL RELATIVA (sin host) Path + Parameter
                .uri(CONTENEDORES_PATH + "/{id}", contenedorId)   
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(ContenedorDTO.class); // RestClient hace el mapping automático
            
            if (contenedor == null) {
                throw new EntidadNotFoundException("Contenedor", "ID " + contenedorId);
            }
            
            return contenedor;
            
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntidadNotFoundException("Contenedor", "ID " + contenedorId);
            
        } catch (HttpClientErrorException e) {
            throw new MicroservicioNoDisponibleException(
                "solicitudes-service", 
                "obtener contenedor ID " + contenedorId + ": " + e.getStatusCode(), 
                e
            );
            
        } catch (Exception e) {
            throw new MicroservicioNoDisponibleException(
                "solicitudes-service", 
                "obtener contenedor ID " + contenedorId, 
                e
            );
        }
    }
    
    // OPCIÓN: Si necesitas obtener contenedor por solicitud
    public ContenedorDTO obtenerContenedorPorSolicitud(Long solicitudId) {
        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            return contenedorRestClient
                .get()
                .uri(CONTENEDORES_PATH + "/por-solicitud/{solicitudId}", solicitudId)   // URL RELATIVA
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(ContenedorDTO.class);
                
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntidadNotFoundException("Contenedor para solicitud", "ID " + solicitudId);
        } catch (Exception e) {
            throw new MicroservicioNoDisponibleException(
                "solicitudes-service", 
                "obtener contenedor por solicitud ID " + solicitudId, 
                e
            );
        }
    }
}