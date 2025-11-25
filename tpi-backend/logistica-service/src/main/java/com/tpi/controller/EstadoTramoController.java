package com.tpi.controller;

import com.tpi.model.EstadoTramo;
import com.tpi.service.EstadoTramoService;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/estados-tramo")
@Tag(
    name = "Estados de Tramo",
    description = "Operaciones CRUD para la gestión de estados de tramo en el sistema"
)
public class EstadoTramoController {

    private final EstadoTramoService estadoTramoService;

    @Operation(
        summary = "Obtener todos los estados de tramo",
        description = "Retorna una lista completa de todos los estados de tramo registrados en el sistema."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de estados de tramo obtenida exitosamente"
    )
    @GetMapping
    public List<EstadoTramo> listarEstadoTramos() {
        return estadoTramoService.findAll();
    }

    @Operation(
        summary = "Crear un nuevo estado de tramo",
        description = "Registra un nuevo estado de tramo en el sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado de tramo creado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        )
    })
    @PostMapping
    public EstadoTramo crearEstadoTramo(
        @Parameter(description = "Datos del estado de tramo a crear")
        @RequestBody @Valid EstadoTramo estadoTramo
    ) {
        return estadoTramoService.save(estadoTramo);
    }

    @Operation(
        summary = "Obtener estado de tramo por ID",
        description = "Busca un estado de tramo específico utilizando su identificador único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado de tramo encontrado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estado de tramo no encontrado"
        )
    })
    @GetMapping("/{id}")
    public EstadoTramo obtenerPorId(
        @Parameter(description = "ID único del estado de tramo", example = "1")
        @PathVariable Long id
    ) {
        return estadoTramoService.findById(id);
    }

    // Opcional: agregar PUT, PATCH y DELETE si el controlador soporta actualización y eliminación
}
