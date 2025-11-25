package com.tpi.controller;

import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Tramo;
import com.tpi.repository.TramoRepository;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tramos")
@Tag(
    name = "Tramos",
    description = "Operaciones CRUD para la gestión de tramos en el sistema"
)
public class TramoController {

    private final TramoRepository tramoRepository;

    public TramoController(TramoRepository tramoRepository) {
        this.tramoRepository = tramoRepository;
    }

    @Operation(
        summary = "Obtener todos los tramos",
        description = "Retorna una lista completa de todos los tramos registrados en el sistema."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de tramos obtenida exitosamente"
    )
    @GetMapping
    public List<Tramo> listarTramos() {
        return tramoRepository.findAll();
    }

    @SuppressWarnings("null")
    @Operation(
        summary = "Crear un nuevo tramo",
        description = "Registra un nuevo tramo en el sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tramo creado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        )
    })
    @PostMapping
    public Tramo crearTramo(
        @Parameter(description = "Datos del tramo a crear")
        @RequestBody @Valid Tramo tramo
    ) {
        return tramoRepository.save(tramo);
    }

    @SuppressWarnings("null")
    @Operation(
        summary = "Obtener un tramo por ID",
        description = "Busca un tramo específico utilizando su identificador único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tramo encontrado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tramo no encontrado"
        )
    })
    @GetMapping("/{id}")
    public Tramo obtenerPorId(
        @Parameter(description = "ID único del tramo", example = "1")
        @PathVariable Long id
    ) {
        return tramoRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Tramo", id));
    }

    // Podés agregar PUT, PATCH y DELETE si necesitás operaciones completas de CRUD
}



