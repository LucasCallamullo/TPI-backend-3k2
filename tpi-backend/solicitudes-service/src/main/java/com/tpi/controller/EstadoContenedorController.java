package com.tpi.controller;

import com.tpi.model.EstadoContenedor;
import com.tpi.service.EstadoContenedorService;
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
@RequestMapping("/api/v1/estados-contenedor")
@RequiredArgsConstructor
@Tag(name = "Estados de Contenedor", description = "API para gestión de estados de contenedor")
public class EstadoContenedorController {

    private final EstadoContenedorService estadoContenedorService;

    @Operation(
        summary = "Obtener todos los estados de contenedor",
        description = "Retorna una lista completa de todos los estados de contenedor disponibles en el sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de estados obtenida exitosamente",
        content = @Content(schema = @Schema(implementation = EstadoContenedor.class))
    )
    @GetMapping
    public ResponseEntity<List<EstadoContenedor>> listarEstados() {
        List<EstadoContenedor> estados = estadoContenedorService.findAll();
        return ResponseEntity.ok(estados);
    }

    @Operation(
        summary = "Obtener estado de contenedor por ID",
        description = "Busca un estado de contenedor específico utilizando su identificador único"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado encontrado",
            content = @Content(schema = @Schema(implementation = EstadoContenedor.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EstadoContenedor> obtenerPorId(
        @Parameter(description = "ID único del estado de contenedor", example = "1")
        @PathVariable Long id) {
        EstadoContenedor estado = estadoContenedorService.findById(id);
        return ResponseEntity.ok(estado);
    }

    @Operation(
        summary = "Obtener estado de contenedor por nombre",
        description = "Busca un estado de contenedor por su nombre exacto"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado encontrado",
            content = @Content(schema = @Schema(implementation = EstadoContenedor.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado"
        )
    })
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<EstadoContenedor> obtenerPorNombre(
        @Parameter(
            description = "Nombre del estado de contenedor", 
            example = "DISPONIBLE"
        )
        @PathVariable String nombre) {
        EstadoContenedor estado = estadoContenedorService.findByNombre(nombre);
        return ResponseEntity.ok(estado);
    }

    @Operation(
        summary = "Crear nuevo estado de contenedor",
        description = "Registra un nuevo estado de contenedor en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Estado creado exitosamente",
            content = @Content(schema = @Schema(implementation = EstadoContenedor.class))
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
    public ResponseEntity<EstadoContenedor> crearEstado(
        @Parameter(description = "Datos del estado de contenedor a crear")
        @Valid @RequestBody EstadoContenedor estadoContenedor) {
            
        EstadoContenedor nuevoEstado = estadoContenedorService.save(estadoContenedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEstado);
    }
}