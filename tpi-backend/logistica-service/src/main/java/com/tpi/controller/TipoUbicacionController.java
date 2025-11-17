package com.tpi.controller;

import com.tpi.model.TipoUbicacion;
import com.tpi.service.TipoUbicacionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tipos-ubicacion")
@Tag(
    name = "Tipos de Ubicación",
    description = "Operaciones CRUD para la gestión de tipos de ubicación en el sistema"
)
public class TipoUbicacionController {

    private final TipoUbicacionService tipoUbicacionService;

    @Operation(
        summary = "Obtener todos los tipos de ubicación",
        description = "Retorna una lista completa de todos los tipos de ubicación registrados en el sistema."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de tipos de ubicación obtenida exitosamente"
    )
    @GetMapping
    public List<TipoUbicacion> listarTiposUbicacion() {
        return tipoUbicacionService.findAll();
    }

    @Operation(
        summary = "Crear nuevo tipo de ubicación",
        description = "Registra un nuevo tipo de ubicación en el sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tipo de ubicación creado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        )
    })
    @PostMapping
    public TipoUbicacion crearTipoUbicacion(
        @Parameter(description = "Datos del tipo de ubicación a crear")
        @RequestBody @Valid TipoUbicacion tipoUbicacion
    ) {
        return tipoUbicacionService.save(tipoUbicacion);
    }

    @Operation(
        summary = "Obtener tipo de ubicación por ID",
        description = "Busca un tipo de ubicación específico utilizando su identificador único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tipo de ubicación encontrado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tipo de ubicación no encontrado"
        )
    })
    @GetMapping("/{id}")
    public TipoUbicacion obtenerPorId(
        @Parameter(description = "ID único del tipo de ubicación", example = "1")
        @PathVariable Long id
    ) {
        return tipoUbicacionService.findById(id);
    }

    @Operation(
        summary = "Actualizar tipo de ubicación",
        description = "Actualiza completamente un tipo de ubicación existente."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tipo de ubicación actualizado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tipo de ubicación no encontrado"
        )
    })
    @PutMapping("/{id}")
    public TipoUbicacion actualizarTipoUbicacion(
        @Parameter(description = "ID del tipo de ubicación a actualizar")
        @PathVariable Long id,
        @Parameter(description = "Datos actualizados del tipo de ubicación")
        @RequestBody @Valid TipoUbicacion tipoUbicacion
    ) {
        return tipoUbicacionService.update(id, tipoUbicacion);
    }

    @Operation(
        summary = "Actualizar tipo de ubicación parcialmente",
        description = "Actualiza campos específicos de un tipo de ubicación existente."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tipo de ubicación actualizado parcialmente exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tipo de ubicación no encontrado"
        )
    })
    @PatchMapping("/{id}")
    public TipoUbicacion actualizarParcialTipoUbicacion(
        @Parameter(description = "ID del tipo de ubicación a actualizar")
        @PathVariable Long id,
        @Parameter(description = "Campos a actualizar")
        @RequestBody Map<String, Object> updates
    ) {
        return tipoUbicacionService.actualizarParcial(id, updates);
    }

    @Operation(
        summary = "Eliminar tipo de ubicación",
        description = "Elimina permanentemente un tipo de ubicación del sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Tipo de ubicación eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tipo de ubicación no encontrado"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoUbicacion(
        @Parameter(description = "ID del tipo de ubicación a eliminar")
        @PathVariable Long id
    ) {
        tipoUbicacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}