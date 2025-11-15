package com.tpi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpi.dto.request.CrearSolicitudCompletaRequestDTO;
import com.tpi.dto.response.EstadoTransporteResponseDTO;
import com.tpi.dto.response.SolicitudResponseDTO;
import com.tpi.service.SolicitudService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.List;

/**
 * Controller para gestionar las operaciones relacionadas con solicitudes de transporte
 * Expone endpoints REST para crear, consultar y actualizar solicitudes
 */
@RestController
@RequestMapping("/api/v1/solicitudes")
@Tag(name = "Solicitudes", description = "API para gestión de solicitudes de transporte")
public class SolicitudController {
    
    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }
    
    /**
     * GET ALL - Consultar todas las solicitudes
     * Retorna lista completa de solicitudes (para Operador/Administrador)
     */
    @Operation(
        summary = "Obtener todas las solicitudes",
        description = "Retorna lista completa de solicitudes. Puede ser filtrada por estado. Acceso para Operador/Administrador"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de solicitudes obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = SolicitudResponseDTO[].class))
        )
    })
    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> obtenerTodasSolicitudes(
            @Parameter(
                description = "Filtrar solicitudes por estado",
                examples = {
                    @ExampleObject(name = "Pendiente", value = "PENDIENTE"),
                    @ExampleObject(name = "Programada", value = "PROGRAMADA"),
                    @ExampleObject(name = "En curso", value = "EN_CURSO"),
                    @ExampleObject(name = "Completada", value = "COMPLETADA"),
                    @ExampleObject(name = "Cancelada", value = "CANCELADA")
                }
            )
            @RequestParam(required = false) String estado) {
        
        List<SolicitudResponseDTO> response = solicitudService.findAll(estado);
        return ResponseEntity.ok(response);
    }

    /**
     * GET - Consultar solicitud por ID
     * Retorna todos los datos de una solicitud específica
     */
    @Operation(
        summary = "Obtener solicitud por ID",
        description = "Recupera la información completa de una solicitud específica mediante su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Solicitud encontrada",
            content = @Content(schema = @Schema(implementation = SolicitudResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solicitud no encontrada"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> obtenerSolicitud(
            @Parameter(
                description = "ID de la solicitud",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
        SolicitudResponseDTO response = solicitudService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH - Actualizar estado de una solicitud
     * URL más RESTful con parámetro de query
     * PATCH /solicitudes/123/estado?estado=PROGRAMADA
     */
    @Operation(
        summary = "Actualizar estado de una solicitud",
        description = "Actualiza el estado de una solicitud específica. Estados válidos: PENDIENTE, PROGRAMADA, EN_CURSO, COMPLETADA, CANCELADA"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Estado de la solicitud actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = SolicitudResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solicitud no encontrada"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Estado inválido o no permitido"
        )
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponseDTO> actualizarEstadoSolicitud(
            @Parameter(
                description = "ID de la solicitud",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @Parameter(
                description = "Nuevo estado de la solicitud",
                examples = {
                    @ExampleObject(name = "Pendiente", value = "PENDIENTE"),
                    @ExampleObject(name = "Programada", value = "PROGRAMADA"),
                    @ExampleObject(name = "En curso", value = "EN_CURSO"),
                    @ExampleObject(name = "Completada", value = "COMPLETADA"),
                    @ExampleObject(name = "Cancelada", value = "CANCELADA")
                },
                required = true
            )
            @RequestParam String estado) {
        
        SolicitudResponseDTO response = solicitudService.actualizarEstado(id, estado);
        return ResponseEntity.ok(response);
    }

    /**
     * POST - Registrar nueva solicitud de transporte completa
     * Recibe todos los datos necesarios y orquesta la creación en ambos microservicios
     * Incluye datos del contenedor, ubicación de origen y ubicación de destino
     */
    @Operation(
        summary = "Crear nueva solicitud de transporte",
        description = "Registra una nueva solicitud de transporte completa. Incluye datos del contenedor, ubicación de origen y destino. Orquesta la creación en ambos microservicios"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201", 
            description = "Solicitud creada exitosamente",
            content = @Content(schema = @Schema(implementation = SolicitudResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto - Contenedor ya existe o datos duplicados"
        )
    })
    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crearSolicitud(
        @Parameter(
            description = "Datos completos para crear la solicitud",
            required = true,
            schema = @Schema(implementation = CrearSolicitudCompletaRequestDTO.class)
        )
        @RequestBody CrearSolicitudCompletaRequestDTO request) {
        
        SolicitudResponseDTO response = solicitudService.crearSolicitudCompleta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET - Consultar estado del transporte de una solicitud
     * Endpoint RESTful para obtener el estado completo del transporte
     */
    @Operation(
        summary = "Consultar estado del transporte",
        description = "Obtiene el estado completo del transporte asociado a una solicitud específica"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Estado del transporte obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = EstadoTransporteResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solicitud o estado de transporte no encontrado"
        )
    })
    @GetMapping("/{solicitudId}/estado-transporte")
    public ResponseEntity<EstadoTransporteResponseDTO> consultarEstadoTransporte(
            @Parameter(
                description = "ID de la solicitud",
                example = "1",
                required = true
            )
            @PathVariable Long solicitudId) {
        
        EstadoTransporteResponseDTO response = solicitudService.obtenerEstadoTransporte(solicitudId);
        return ResponseEntity.ok(response);
    }
}

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
