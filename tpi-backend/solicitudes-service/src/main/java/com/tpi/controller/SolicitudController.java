package com.tpi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tpi.service.SolicitudService;
import com.tpi.dto.SolicitudDTOs.*;

import java.util.List;

/**
 * Controller para gestionar las operaciones relacionadas con solicitudes de transporte
 * Expone endpoints REST para crear, consultar y actualizar solicitudes
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    
    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    /**
     * POST - Registrar nueva solicitud de transporte
     * Crea una nueva solicitud incluyendo el contenedor y cliente si no existe
     */
    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crearSolicitud(@RequestBody CrearSolicitudRequestDTO request) {
        SolicitudResponseDTO response = solicitudService.crearSolicitud(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * GET - Consultar estado del transporte de un contenedor
     * Retorna el estado actual y historial de seguimiento
     */
    @GetMapping("/{id}/estado")
    public ResponseEntity<EstadoSolicitudResponseDTO> consultarEstado(@PathVariable String id) {
        EstadoSolicitudResponseDTO response = solicitudService.obtenerEstadoSolicitud(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET - Consultar solicitud por ID
     * Retorna todos los datos de una solicitud específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> obtenerSolicitud(@PathVariable String id) {
        SolicitudResponseDTO response = solicitudService.obtenerSolicitudPorId(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT - Asignar ruta a solicitud
     * Asigna una ruta completa con todos sus tramos a una solicitud existente
     */
    @PutMapping("/{id}/ruta")
    public ResponseEntity<SolicitudResponseDTO> asignarRuta(
        @PathVariable String id, 
        @RequestBody AsignarRutaRequestDTO request) {
        SolicitudResponseDTO response = solicitudService.asignarRuta(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET - Consultar todas las solicitudes con filtros
     * Permite filtrar por estado, cliente o contenedor
     */
    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> consultarSolicitudes(
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String clienteId,
        @RequestParam(required = false) String contenedorId) {
        List<SolicitudResponseDTO> response = solicitudService.obtenerSolicitudesConFiltros(estado, clienteId, contenedorId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT - Actualizar estado de solicitud usando estadoId
     * Actualiza el estado de una solicitud mediante el ID del estado (no el nombre)
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponseDTO> actualizarEstado(
        @PathVariable String id,
        @RequestBody ActualizarEstadoRequestDTO request) {
        SolicitudResponseDTO response = solicitudService.actualizarEstado(id, request.estadoId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT - Registrar cálculo final de tiempo y costo real
     * Finaliza la solicitud registrando los valores reales de tiempo y costo
     */
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<SolicitudResponseDTO> finalizarSolicitud(
        @PathVariable String id,
        @RequestBody FinalizarSolicitudRequestDTO request) {
        SolicitudResponseDTO response = solicitudService.finalizarSolicitud(id, request);
        return ResponseEntity.ok(response);
    }
}