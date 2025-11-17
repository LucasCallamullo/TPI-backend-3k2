package com.tpi.controller;

import com.tpi.model.EstadoSolicitud;
import com.tpi.service.EstadoSolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/estados-solicitud")
@RequiredArgsConstructor
@Tag(name = "Estados de Solicitud", description = "API para gestión de estados de solicitud")
public class EstadoSolicitudController {

    private final EstadoSolicitudService estadoSolicitudService;

    @Operation(
        summary = "Obtener todos los estados de solicitud",
        description = "Retorna una lista completa de todos los estados de solicitud disponibles en el sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de estados obtenida exitosamente",
        content = @Content(schema = @Schema(implementation = EstadoSolicitud.class))
    )
    @GetMapping
    public ResponseEntity<List<EstadoSolicitud>> listarEstados() {
        List<EstadoSolicitud> estados = estadoSolicitudService.findAll();
        return ResponseEntity.ok(estados);
    }

    @Operation(
        summary = "Obtener estado de solicitud por ID",
        description = "Busca un estado de solicitud específico utilizando su identificador único"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado encontrado",
            content = @Content(schema = @Schema(implementation = EstadoSolicitud.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EstadoSolicitud> obtenerPorId(
        @Parameter(description = "ID único del estado de solicitud", example = "1")
        @PathVariable Long id) {
        EstadoSolicitud estado = estadoSolicitudService.findById(id);
        return ResponseEntity.ok(estado);
    }

    @Operation(
        summary = "Obtener estado de solicitud por nombre",
        description = "Busca un estado de solicitud por su nombre exacto"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado encontrado",
            content = @Content(schema = @Schema(implementation = EstadoSolicitud.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado"
        )
    })
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<EstadoSolicitud> obtenerPorNombre(
        @Parameter(description = "Nombre del estado de solicitud", example = "BORRADOR")
        @PathVariable String nombre) {
        EstadoSolicitud estado = estadoSolicitudService.findByNombre(nombre);
        return ResponseEntity.ok(estado);
    }

    @Operation(
        summary = "Crear nuevo estado de solicitud",
        description = "Registra un nuevo estado de solicitud en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Estado creado exitosamente",
            content = @Content(schema = @Schema(implementation = EstadoSolicitud.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Ya existe un estado con ese nombre"
        )
    })
    @PostMapping
    public ResponseEntity<EstadoSolicitud> crearEstado(
        @Parameter(description = "Datos del estado de solicitud a crear")
        @Valid @RequestBody EstadoSolicitud estadoSolicitud) {
        EstadoSolicitud nuevoEstado = estadoSolicitudService.save(estadoSolicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEstado);
    }
}