package com.tpi.controller;

import com.tpi.dto.request.ContenedorRequestDTO;
import com.tpi.dto.response.ContenedorResponseDTO;
import com.tpi.service.ContenedorService;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/v1/contenedores")
@RequiredArgsConstructor
@Tag(name = "Contenedores", description = "API para gestión de contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    /**
     * GET - Obtener todos los contenedores
     * # Todos los contenedores
     */
    @Operation(
        summary = "Obtener todos los contenedores",
        description = "Retorna una lista de contenedores. Puede ser filtrada por estado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de contenedores obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ContenedorResponseDTO[].class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Estado de filtro inválido"
        )
    })
    @GetMapping
    public ResponseEntity<List<ContenedorResponseDTO>> obtenerTodosContenedores(
        @Parameter(
            description = "Filtrar contenedores por estado",
            examples = {
                @ExampleObject(name = "Disponible", value = "DISPONIBLE"),
                @ExampleObject(name = "En tránsito", value = "EN_TRANSITO"),
                @ExampleObject(name = "Entregado", value = "ENTREGADO")
            }
        )
        @RequestParam(required = false) String estado) {
            
        List<ContenedorResponseDTO> contenedores = contenedorService.findAll(estado);
        return ResponseEntity.ok(contenedores);
    }

    /**
     * GET - Obtener contenedor por ID
     */
    @Operation(
        summary = "Obtener contenedor por ID",
        description = "Recupera la información de un contenedor específico mediante su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Contenedor encontrado",
            content = @Content(schema = @Schema(implementation = ContenedorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Contenedor no encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContenedorResponseDTO> obtenerContenedor(
        @Parameter(
            description = "ID del contenedor",
            example = "1",
            required = true
        )
        @PathVariable Long id) {
        
        ContenedorResponseDTO contenedor = contenedorService.findById(id);
        return ResponseEntity.ok(contenedor);
    }

    /**
     * PATCH - Actualizar estado de un contenedor
     */
    @Operation(
        summary = "Actualizar estado de un contenedor",
        description = "Actualiza el estado de un contenedor específico. Estados válidos: DISPONIBLE, EN_TRANSITO, ENTREGADO"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Estado del contenedor actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = ContenedorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Contenedor no encontrado"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Estado inválido o no permitido"
        )
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ContenedorResponseDTO> actualizarEstadoContenedor(
            @Parameter(
                description = "ID del contenedor",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @Parameter(
                description = "Nuevo estado del contenedor",
                examples = {
                    @ExampleObject(name = "Disponible", value = "DISPONIBLE"),
                    @ExampleObject(name = "En tránsito", value = "EN_TRANSITO"), 
                    @ExampleObject(name = "Entregado", value = "ENTREGADO")
                },
                required = true
            )
            @RequestParam String estado) {
        ContenedorResponseDTO contenedor = contenedorService.actualizarEstado(id, estado);
        return ResponseEntity.ok(contenedor);
    }


    @Operation(summary = "Crear nuevo contenedor")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Contenedor creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ContenedorResponseDTO> crearContenedor(
            @Valid @RequestBody ContenedorRequestDTO contenedorRequest) {
        ContenedorResponseDTO contenedor = contenedorService.crearContenedor(contenedorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(contenedor);
    }
}
