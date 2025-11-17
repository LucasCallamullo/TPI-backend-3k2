package com.tpi.client;

import com.tpi.dto.external.CostoFinalDTO;
import com.tpi.dto.external.CostosEstimadosDTO;
import com.tpi.dto.external.RutaResponses.RutaAsignadaResponseDTO;
import com.tpi.dto.external.RutaResponses.RutaTramosCamionResponse;
import com.tpi.dto.external.UbicacionResponses.UbicacionResponseDTO;

import com.tpi.dto.request.CrearRutaCompletaRequest;
import com.tpi.dto.request.UbicacionRequestDTO;

import com.tpi.exception.EntidadNotFoundException;
import com.tpi.exception.MicroservicioNoDisponibleException;
import com.tpi.service.SecurityContextService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class LogisticaServiceClient {
    
    private static final String UBICACIONES_PATH = "/api/v1/ubicaciones";
    private static final String RUTAS_PATH = "/api/v1/rutas";

    private final SecurityContextService securityContextService;
    private final RestClient logisticaRestClient;

    /*
     * Se utiliza para crear una ubicacion para asociar a la solicitud 
     */
    @SuppressWarnings("null")
    public Long crearUbicacion(UbicacionRequestDTO ubicacionRequest) {

        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            String response = logisticaRestClient
                .post()
                // URL RELATIVA (sin host) BASE_URL + Path
                .uri(UBICACIONES_PATH)   
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(ubicacionRequest)
                .retrieve()
                .body(String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response);
            return jsonResponse.get("id").asLong();


        } catch (HttpClientErrorException.NotFound e) {
            //  Si es 404 (tipo no encontrado)
            throw new EntidadNotFoundException(
                "ubicación", "ID " + ubicacionRequest.tipoId());
            
        } catch (HttpClientErrorException e) {
            // Otros errores HTTP (400, 401, 403, etc.)
            throw new MicroservicioNoDisponibleException(
                "logistica-service", "crear ubicación: " + e.getStatusCode(), e);
            
        } catch (Exception e) {
            // Errores genéricos (timeout, conexión, etc.)
            throw new MicroservicioNoDisponibleException(
                "logistica-service", "crear ubicación", e);
        }
    }

    /**
     * Obitnee una ubicacion por el endpotin get id
     */
    public UbicacionResponseDTO obtenerUbicacionPorId(Long ubicacionId) {
        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            UbicacionResponseDTO response = logisticaRestClient
                .get()
                .uri(UBICACIONES_PATH + "/" + ubicacionId)   // GET /api/v1/ubicaciones/{id}
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(UbicacionResponseDTO.class);  // ← Devuelve UN solo DTO
                
            return response;
            
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntidadNotFoundException("ubicación", "ID " + ubicacionId);
            
        } catch (HttpClientErrorException e) {
            throw new MicroservicioNoDisponibleException(
                "logistica-service", 
                "obtener ubicación: " + e.getStatusCode(), 
                e
            );
            
        } catch (Exception e) {
            throw new MicroservicioNoDisponibleException(
                "logistica-service", 
                "obtener ubicación", 
                e
            );
        }
    }

    /*
     * Se utiliza para asginar una ruta a la solicitud
     */
    @SuppressWarnings("null")
    public RutaAsignadaResponseDTO crearRutaParaSolicitud(
        CrearRutaCompletaRequest request) {
        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            RutaAsignadaResponseDTO response = logisticaRestClient
                .post()
                .uri(RUTAS_PATH + "/asignar-a-solicitud")   
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(request)
                .retrieve()
                .body(RutaAsignadaResponseDTO.class);
                
            return response;
            
        } catch (HttpClientErrorException e) {
            throw new MicroservicioNoDisponibleException(
                "MS-Logística: ", "error en fetch a rutas" , e);
        }
    }

    /**
     * Se comunica con end point del ms-logistica para obtener ruta, tramos asociados a la solicitud
     */
    public RutaTramosCamionResponse obtenerRutaPorSolicitudId(Long solicitudId) {
        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            RutaTramosCamionResponse response = logisticaRestClient
                .get()  // ← Cambiar POST por GET
                .uri(RUTAS_PATH + "/solicitud/" + solicitudId)   // ← URL correcta
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(RutaTramosCamionResponse.class);
                
            return response;
            
        } catch (HttpClientErrorException e) {
            throw new MicroservicioNoDisponibleException(
                "MS-Logística: ", "error en fetch a rutas" , e);
        }
    }


    /*
     * Calcular costos estimados
     */
    public CostosEstimadosDTO calcularCostosEstimados(Long solicitudId) {
        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            CostosEstimadosDTO response = logisticaRestClient
                .get() 
                .uri(RUTAS_PATH + "/solicitud/" + solicitudId + "/calcular-costos-estimados") 
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(CostosEstimadosDTO.class);
                
            return response;
            
        } catch (HttpClientErrorException e) {
            throw new MicroservicioNoDisponibleException(
                "MS-Logística: ", "error en fetch a rutas" , e);
        }
    }


    /*
     * Calcular costos Totales
     */
    public CostoFinalDTO calcularCostosTotales(Long solicitudId) {
        try {
            String jwtToken = securityContextService.obtenerJwtToken();
            
            CostoFinalDTO response = logisticaRestClient
                .get()  
                .uri(RUTAS_PATH + "/solicitud/" + solicitudId + "/calcular-costos-totales") 
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(CostoFinalDTO.class);
                
            return response;
            
        } catch (HttpClientErrorException e) {
            throw new MicroservicioNoDisponibleException(
                "MS-Logística: ", "error en fetch a rutas" , e);
        }
    }
}