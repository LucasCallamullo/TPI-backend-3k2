package com.tpi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpi.dto.request.CrearSolicitudCompletaRequestDTO;
import com.tpi.dto.response.EstadoTransporteResponseDTO;
import com.tpi.dto.response.SolicitudResponseDTO;
import com.tpi.service.SolicitudService;


/**
 * Controller para gestionar las operaciones relacionadas con solicitudes de transporte
 * Expone endpoints REST para crear, consultar y actualizar solicitudes
 */
@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController {
    
    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }
    
    // ✅ ENDPOINT PÚBLICO PARA PROBAR LA CONEXIÓN CON KEYCLOAK
    @GetMapping("/publico")
    public String probarKeycloak() {
        return "✅ Hola desde Solicitudes - Conexión con Keycloak funcionando!";
    }

    // ✅ ENDPOINT PROTEGIDO PARA PROBAR AUTENTICACIÓN
    @GetMapping("/protegido")
    public String endpointProtegido() {
        return "🔒 Este es un endpoint protegido en Solicitudes - Solo con token válido!";
    }


    /**
     * GET ALL - Consultar todas las solicitudes
     * Retorna lista completa de solicitudes (para Operador/Administrador)
     */
    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> obtenerTodasSolicitudes(
            @RequestParam(required = false) String estado) {
        
        List<SolicitudResponseDTO> response = solicitudService.findAll(estado);
        return ResponseEntity.ok(response);
    }

    /**
     * GET - Consultar solicitud por ID
     * Retorna todos los datos de una solicitud específica
    */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> obtenerSolicitud(@PathVariable Long id) {
        SolicitudResponseDTO response = solicitudService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH - Actualizar estado de una solicitud
     * URL más RESTful con parámetro de query
     * PATCH /solicitudes/123/estado?estado=PROGRAMADA
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponseDTO> actualizarEstadoSolicitud(
            @PathVariable Long id,
            @RequestParam String estado) {
        
        SolicitudResponseDTO response = solicitudService.actualizarEstado(id, estado);
        return ResponseEntity.ok(response);
    }

    /**
     * POST - Registrar nueva solicitud de transporte completa
     * Recibe todos los datos necesarios y orquesta la creación en ambos microservicios
     * Incluye datos del contenedor, ubicación de origen y ubicación de destino
     */
    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crearSolicitud(
        @RequestBody CrearSolicitudCompletaRequestDTO request) {
        
        SolicitudResponseDTO response = solicitudService.crearSolicitudCompleta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET - Consultar estado del transporte de una solicitud
     * Endpoint RESTful para obtener el estado completo del transporte
     */
    @GetMapping("/{solicitudId}/estado-transporte")
    public ResponseEntity<EstadoTransporteResponseDTO> consultarEstadoTransporte(
            @PathVariable Long solicitudId) {
        
        EstadoTransporteResponseDTO response = solicitudService.obtenerEstadoTransporte(solicitudId);
        return ResponseEntity.ok(response);
    }




    /**

    
    
    /**
     * PUT - Asignar ruta a solicitud
     * Asigna una ruta completa con todos sus tramos a una solicitud existente

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

    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> consultarSolicitudes(
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String clienteId,
        @RequestParam(required = false) String contenedorId) {
        List<SolicitudResponseDTO> response = solicitudService.obtenerSolicitudesConFiltros(estado, clienteId, contenedorId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT - Registrar cálculo final de tiempo y costo real
     * Finaliza la solicitud registrando los valores reales de tiempo y costo
  
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<SolicitudResponseDTO> finalizarSolicitud(
        @PathVariable String id,
        @RequestBody FinalizarSolicitudRequestDTO request) {
        SolicitudResponseDTO response = solicitudService.finalizarSolicitud(id, request);
        return ResponseEntity.ok(response);
    } */
}