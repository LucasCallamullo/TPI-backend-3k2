package com.tpi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

import com.tpi.client.LogisticaServiceClient;
import com.tpi.dto.external.CostoFinalDTO;
import com.tpi.dto.external.CostosEstimadosDTO;
import com.tpi.dto.external.RutaResponses.RutaAsignadaResponseDTO;
import com.tpi.dto.external.RutaResponses.RutaTramosCamionResponse;
import com.tpi.dto.external.UbicacionResponses.UbicacionResponseDTO;
import com.tpi.dto.request.AsignarRutaRequest;
import com.tpi.dto.request.CrearRutaCompletaRequest;
import com.tpi.dto.request.SolicitudCompletaRequestDTO;
import com.tpi.dto.response.ContenedorResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudWithRutaResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudWithUbicacionAndRutaResponseDTO;
import com.tpi.exception.AccessoDenegadoException;
import com.tpi.exception.EntidadNotFoundException;

// Entidades y Repositorios
import com.tpi.model.Solicitud;
import com.tpi.model.EstadoSolicitud;
import com.tpi.model.Contenedor;

import com.tpi.repository.SolicitudRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor    // crea builder con todos los campos marcados en final, evitar builder verboso
public class SolicitudService {
    
    private final SolicitudRepository solicitudRepository;
    private final ContenedorService contenedorService;
    private final EstadoSolicitudService estadoSolicitudService;
    private final LogisticaServiceClient logisticaServiceClient;
    private final SecurityContextService securityContextService;
    
    /**
     * Metodo para crear solicitud con solo contenedor en la peticion
     */
    @SuppressWarnings("null")
    public SolicitudResponseDTO crearSolicitudCompleta(
        SolicitudCompletaRequestDTO request, String keycloakId) {
        
        // 2. Crear ubicaciones a través del cliente especializado
        Long origenId = logisticaServiceClient.crearUbicacion(request.origen());
        Long destinoId = logisticaServiceClient.crearUbicacion(request.destino());
        
        // 3. Crear contenedor
        Contenedor contenedor = contenedorService.getOrCreate(request.contenedor(), keycloakId);
        
        // 4. Obtener estado
        EstadoSolicitud estadoBorrador = estadoSolicitudService.findByNombre("BORRADOR");
        
        // 5. Crear solicitud (SOLO lógica de dominio)
        Solicitud solicitud = Solicitud.builder()
            .clienteId(keycloakId)    // Keycloack Id utilizado como id en base de datos
            .contenedor(contenedor)
            .origenId(origenId)
            .destinoId(destinoId)
            .estado(estadoBorrador)
            .costoEstimado(0.0)
            .tiempoEstimado(0.0)
            .build();
            
        Solicitud saved = solicitudRepository.save(solicitud);
        
        return SolicitudResponseDTO.fromEntity(saved);
    }

    /**
     * Solo actualiza estado
     */
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
    

    /*
     * Se encarga de consultar el estado actual de la solicitud y su ruta y tramos asociados
     */
    public SolicitudWithUbicacionAndRutaResponseDTO seguimientoSolicitud(Long solicitudId) {
        // 1. Validar que el cliente solo pueda ver SUS propias solicitudes
        String clienteId = securityContextService.obtenerClienteIdDesdeToken();
        Solicitud solicitud = this.findByIdAndClienteId(solicitudId, clienteId);
        
        // Obtener ubicaciones desde MS-LOGISTICA
        UbicacionResponseDTO origen = logisticaServiceClient.obtenerUbicacionPorId(solicitud.getOrigenId());
        UbicacionResponseDTO destino = logisticaServiceClient.obtenerUbicacionPorId(solicitud.getDestinoId());

        // 2. Llamar a MS-LOGISTICA para obtener la ruta y tramos
        RutaTramosCamionResponse ruta = logisticaServiceClient.obtenerRutaPorSolicitudId(solicitudId);
        
        return SolicitudWithUbicacionAndRutaResponseDTO.fromEntity(
            solicitud, origen, destino, ruta 
        );
    };

    
    
    /*
     * Asignar Ruta a una solicitud
     */
    public SolicitudWithRutaResponseDTO asignarRuta(Long id, AsignarRutaRequest request) {

        // 1. Obtener solicitud existente
        Solicitud solicitud = this.findById(id);
        
        // 2. crear request a ruta
        CrearRutaCompletaRequest rutaRequest = new CrearRutaCompletaRequest(
            id, // solicitudId
            solicitud.getOrigenId(),        // referencias a ubicaciones en ms-logistica
            solicitud.getDestinoId(),         // referencias a ubicaciones en ms-logistica
            request.tarifaId(),            // tarifa elegegida por el cliente
            request.depositosIntermedios()    // depositos intermedios elegidos por el operador
        );

        // 3. Llamar a MS-Logística para crear la ruta
        RutaAsignadaResponseDTO rutaAsignada = logisticaServiceClient.crearRutaParaSolicitud(rutaRequest);

        // 4. Obtener estado
        EstadoSolicitud estadoProgramada = estadoSolicitudService.findByNombre("PROGRAMADA");
        
        // 5. Actualizar solicitud con la ruta asignada
        solicitud.setEstado(estadoProgramada);
        solicitud.setCostoEstimado(rutaAsignada.distanciaTotal());
        solicitud.setTiempoEstimado(rutaAsignada.duracionTotalHoras());
        this.save(solicitud);
        
        return SolicitudWithRutaResponseDTO.fromEntity(solicitud, rutaAsignada);
    }


    /*
    * Calcular costos estimados y asignarlos a la solicitud
    */
    @SuppressWarnings("null")
    public CostosEstimadosDTO calcularCostosEstimados(Long solicitudId) {
        log.info("Calculando costos estimados para solicitud ID: {}", solicitudId);
        
        CostosEstimadosDTO costos = logisticaServiceClient.calcularCostosEstimados(solicitudId);
        
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new EntidadNotFoundException("Solicitud", solicitudId));
        
        solicitud.setCostoEstimado(costos.getCostoTotal());
        solicitud.setTiempoEstimado(costos.getTiempoEstimadoSegundos()/3600.0);
        this.save(solicitud);


        log.info("Costos estimados calculados exitosamente para solicitud ID: {}", solicitudId);
        return costos;
    }


    /*
    * Calcular costos estimados y asignarlos a la solicitud
    */
    @SuppressWarnings("null")
    public CostoFinalDTO calcularCostosTotales(Long solicitudId) {
        log.info("Calculando costos estimados para solicitud ID: {}", solicitudId);
        
        CostoFinalDTO costos = logisticaServiceClient.calcularCostosTotales(solicitudId);
        
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new EntidadNotFoundException("Solicitud", solicitudId));
        
        solicitud.setCostoFinal(costos.getCostoTotal());
        solicitud.setTiempoReal(costos.getTiempoTotalSegundos()/3600.0);
        this.save(solicitud);

        log.info("Costos estimados calculados exitosamente para solicitud ID: {}", solicitudId);
        return costos;
    }


    @SuppressWarnings("null")
    public ContenedorResponseDTO obtenerContenedor(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new EntidadNotFoundException("Solicitud", solicitudId));
        
        Contenedor contenedor = solicitud.getContenedor();
        return ContenedorResponseDTO.fromEntity(contenedor); 
    }


    /*
     * Guardar solicitud
     */
    @SuppressWarnings("null")
    public Solicitud save(Solicitud e) {
        return this.solicitudRepository.save(e);
    }

    /*
     * Encontrar solo solicitudes del mismo cliente
     */
    public Solicitud findByIdAndClienteId(Long solicitudId, String clienteId) {
        return solicitudRepository.findByIdAndClienteId(solicitudId, clienteId)
            .orElseThrow(() -> new AccessoDenegadoException("No tiene acceso a esta solicitud"));
    };

    /**
     * Para solicitud GET por Solicitud
     */
    @SuppressWarnings("null")
    public SolicitudResponseDTO getDTOById(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Solicitud no encontrada con ID: " + id
            ));
        return SolicitudResponseDTO.fromEntity(solicitud);
    }

    /*
     * Encontrar por id
     */
    @SuppressWarnings("null")
    public Solicitud findById(Long id) {
        return solicitudRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("Solicitud", id));
    }

    /**
     * para obtener todas las solicitudes sin paginacion de momento
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
}