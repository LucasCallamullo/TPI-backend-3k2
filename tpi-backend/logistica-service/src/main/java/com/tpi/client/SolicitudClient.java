package com.tpi.client;

import com.tpi.dto.external.ContenedorResponseDTO;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.exception.MicroservicioNoDisponibleException;
// import com.tpi.service.SecurityContextService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolicitudClient {
    
    // URL para OBTENER contenedor (no crear ubicación)
    private static final String SOLICITUD_PATH = "/api/v1/solicitudes"; //  Solo el path

    // private final SecurityContextService securityContextService;
    private final RestClient solicitudesRestClient;     // Nombre coincide con bean en RestConfig

    public ContenedorResponseDTO obtenerInfoContenedor(Long solicitudId) {
        log.info("Obteniendo información del contenedor para solicitud ID: {}", solicitudId);
        
        try {
            ContenedorResponseDTO solicitud = solicitudesRestClient.get()
                .uri(SOLICITUD_PATH + "/{solicitudId}/contenedor", solicitudId)
                .retrieve()
                .body(ContenedorResponseDTO.class);
            
            return solicitud;
            
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Solicitud no encontrada: {}", solicitudId);
            throw new EntidadNotFoundException("Solicitud", solicitudId);
        } catch (Exception e) {
            log.error("Error al obtener solicitud {}: {}", solicitudId, e.getMessage());
            throw new MicroservicioNoDisponibleException(
                "Error al obtener solicitud: ", e.getMessage(), e);
        }
    }
}
