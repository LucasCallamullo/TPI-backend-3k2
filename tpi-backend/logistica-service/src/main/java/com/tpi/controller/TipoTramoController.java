package com.tpi.controller;

import com.tpi.model.TipoTramo;
import com.tpi.service.TipoTramoService;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/v1/tipo-tramos")
@Tag(
    name = "Tipos de Tramo",
    description = "Operaciones CRUD para la gestión de tipos de tramo en el sistema"
)
public class TipoTramoController {

    private final TipoTramoService tipoTramoService;

    public TipoTramoController(TipoTramoService tipoTramoService) {
        this.tipoTramoService = tipoTramoService;
    }

    @Operation(
        summary = "Obtener todos los tipos de tramo",
        description = "Retorna una lista completa de todos los tipos de tramo registrados en el sistema."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de tipos de tramo obtenida exitosamente"
    )
    @GetMapping
    public List<TipoTramo> listarTipoTramos() {
        return tipoTramoService.findAll();
    }

    @Operation(
        summary = "Crear un nuevo tipo de tramo",
        description = "Registra un nuevo tipo de tramo en el sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tipo de tramo creado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        )
    })
    @PostMapping
    public TipoTramo crearTipoTramo(
        @Parameter(description = "Datos del tipo de tramo a crear")
        @RequestBody @Valid TipoTramo tipoTramo
    ) {
        return tipoTramoService.save(tipoTramo);
    }

    @Operation(
        summary = "Obtener tipo de tramo por ID",
        description = "Busca un tipo de tramo específico utilizando su identificador único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tipo de tramo encontrado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tipo de tramo no encontrado"
        )
    })
    @GetMapping("/{id}")
    public TipoTramo findById(
        @Parameter(description = "ID único del tipo de tramo", example = "1")
        @PathVariable Long id
    ) {
        return tipoTramoService.findById(id);
    }

    // Podés agregar PUT, PATCH y DELETE con la misma lógica si el servicio los soporta
}

