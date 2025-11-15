package com.tpi.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpi.dto.request.CrearSolicitudCompletaRequestDTO;
import com.tpi.dto.request.UbicacionRequestDTO;
import com.tpi.dto.response.EstadoTransporteResponseDTO;
import com.tpi.dto.response.SolicitudResponseDTO;

import com.tpi.exception.AccessoDenegadoException;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.exception.MicroservicioNoDisponibleException;

// Entidades y Repositorios
import com.tpi.model.Solicitud;
import com.tpi.model.EstadoSolicitud;
import com.tpi.model.Contenedor;

import com.tpi.repository.SolicitudRepository;


@Service
@Transactional
public class SolicitudService {
    
    private final SolicitudRepository solicitudRepository;
    private final ContenedorService contenedorService;
    private final EstadoSolicitudService estadoSolicitudService;
    private static final String LOGISTICA_URL = "http://localhost:8081/api/ubicaciones";
    
    // No necesitamos RestTemplate, usamos RestClient directamente
    public SolicitudService(SolicitudRepository solicitudRepository,
                          ContenedorService contenedorService,
                          EstadoSolicitudService estadoSolicitudService) {
        this.solicitudRepository = solicitudRepository;
        this.estadoSolicitudService = estadoSolicitudService;
        this.contenedorService = contenedorService;
    }

    
    /**
     * Para solicitud GET por Solicitud
     * @param id
     * @return
     */
    @SuppressWarnings("null")
    public SolicitudResponseDTO findById(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Solicitud no encontrada con ID: " + id
            ));
        return SolicitudResponseDTO.fromEntity(solicitud);
    }

    /**
     * para obtener todas las solicitudes sin paginacion de momento
     * @param estado
     * @return
     */
    public List<SolicitudResponseDTO> findAll(String estado) {
        List<Solicitud> solicitudes;
        
        if (estado != null && !estado.trim().isEmpty()) {
            solicitudes = solicitudRepository.findByEstadoNombre(estado);
        } else {
            solicitudes = solicitudRepository.findAll();
        }
        
        return solicitudes.stream()
                .map(SolicitudResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    public SolicitudResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        Solicitud solicitud = solicitudRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Solicitud no encontrada con ID: " + id
            ));
        
        // Buscar el nuevo estado
        EstadoSolicitud estado = estadoSolicitudService.findByNombre(nuevoEstado);
        
        solicitud.setEstado(estado);
        Solicitud updated = solicitudRepository.save(solicitud);
        
        return SolicitudResponseDTO.fromEntity(updated);
    }



    @SuppressWarnings("null")
    public SolicitudResponseDTO crearSolicitudCompleta(CrearSolicitudCompletaRequestDTO request) {
    
        // 1. Crear ubicación ORIGEN en ms-logistica (con JWT)
        Long origenId = crearUbicacionEnLogistica(request.origen());
        
        // 2. Crear ubicación DESTINO en ms-logistica (con JWT)  
        Long destinoId = crearUbicacionEnLogistica(request.destino());
        
        // 3. Crear contenedor NUEVO (según requerimiento)
        Contenedor contenedorGuardado = contenedorService.crearContenedorEntidad(request.contenedor());
        
        // 4. Obtener estado inicial de solicitud
        EstadoSolicitud estadoBorrador = estadoSolicitudService.findByNombre("BORRADOR");
        
        // 5. Obtener clienteId del contexto de seguridad
        String clienteId = obtenerClienteIdDesdeToken();
        
        // 6. Crear solicitud
        Solicitud solicitud = Solicitud.builder()
            .clienteId(clienteId)
            .contenedor(contenedorGuardado)
            .origenId(origenId)
            .destinoId(destinoId)
            .estado(estadoBorrador)
            .costoEstimado(BigDecimal.ZERO)
            .tiempoEstimado(0)
            .build();
            
        
        Solicitud saved = solicitudRepository.save(solicitud);
        
        // 7. Retornar usando el constructor estático del DTO
        return SolicitudResponseDTO.fromEntity(saved, origenId, destinoId);
    }

    private Long crearUbicacionEnLogistica(UbicacionRequestDTO ubicacionRequest) {
        try {
            // 1. Obtener el JWT del contexto de seguridad
            String jwtToken = obtenerJwtToken();
            
            // 2. Usar RestClient (como en tu gateway)
            @SuppressWarnings("null")
            String response = RestClient.create()
                .post()
                .uri(LOGISTICA_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(ubicacionRequest)
                .retrieve()
                .body(String.class);
            
            // 3. Parsear la respuesta para obtener el ID
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response);
            return jsonResponse.get("id").asLong();
            
        } catch (Exception e) {
            throw new MicroservicioNoDisponibleException(
                "ms-logistica", "crear ubicación", e
            );
        }
    }

    /**
     * Obtiene el ID del cliente desde el token JWT de autenticación
     * El clienteId se extrae del subject del token JWT
     */
    private String obtenerClienteIdDesdeToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getSubject();
        }
        throw new RuntimeException("Usuario no autenticado");
    }

    /**
     * Obtiene el token JWT completo desde el contexto de seguridad de Spring
     * Primero intenta obtenerlo desde credentials y luego desde el principal
     */
    private String obtenerJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getTokenValue();
        }
        throw new RuntimeException("No se pudo obtener el JWT token");
    }

    public EstadoTransporteResponseDTO obtenerEstadoTransporte(Long solicitudId) {
        // 1. Validar que el cliente tiene acceso
        String clienteId = obtenerClienteIdDesdeToken();
        Solicitud solicitud = obtenerSolicitudConAcceso(solicitudId, clienteId);
        
        // 2. Mapear a DTO de respuesta
        return EstadoTransporteResponseDTO.fromEntity(solicitud);
    }
    
    @SuppressWarnings("null")
    private Solicitud obtenerSolicitudConAcceso(Long solicitudId, String clienteId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new EntidadNotFoundException("Solicitud", solicitudId));
            
        if (!solicitud.getClienteId().equals(clienteId)) {
            throw new AccessoDenegadoException("No tiene acceso a esta solicitud");
        }
        
        return solicitud;
    }


    /**
     * Cambia el estado de una solicitud

    public Solicitud cambiarEstado(Long solicitudId, String nuevoEstadoNombre) {
        @SuppressWarnings("null")
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        EstadoSolicitud nuevoEstado = estadoSolicitudService.findByNombre(nuevoEstadoNombre)
            .orElseThrow(() -> new RuntimeException("Estado " + nuevoEstadoNombre + " no encontrado"));

        solicitud.setEstado(nuevoEstado);
        return solicitudRepository.save(solicitud);
    }

    /**
     * Actualiza costos y tiempos estimados

    public Solicitud actualizarEstimaciones(Long solicitudId, BigDecimal costoEstimado, Integer tiempoEstimado) {
        @SuppressWarnings("null")
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setCostoEstimado(costoEstimado);
        solicitud.setTiempoEstimado(tiempoEstimado);

        return solicitudRepository.save(solicitud);
    }

    /**
     * Registra costos y tiempos reales al finalizar

    public Solicitud finalizarSolicitud(Long solicitudId, BigDecimal costoFinal, Integer tiempoReal) {
        @SuppressWarnings("null")
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setCostoFinal(costoFinal);
        solicitud.setTiempoReal(tiempoReal);

        // Cambiar estado a ENTREGADA
        EstadoSolicitud estadoEntregada = estadoSolicitudService.findByNombre("ENTREGADA")
            .orElseThrow(() -> new RuntimeException("Estado ENTREGADA no encontrado"));
        solicitud.setEstado(estadoEntregada);

        return solicitudRepository.save(solicitud);
    }

    /**
     * Valida si ya existe una solicitud con el mismo contenedor
     
    public boolean existeSolicitudConContenedor(String identificacionUnica) {
        return solicitudRepository.existsByContenedorIdentificacionUnica(identificacionUnica);
    } */





}