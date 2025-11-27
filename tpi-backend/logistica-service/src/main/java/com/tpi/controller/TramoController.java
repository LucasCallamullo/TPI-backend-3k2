package com.tpi.controller;

import com.tpi.dto.response.RutasTramosCamionResponsesDTO.TramoConDetalles;
import com.tpi.model.Tramo;
import com.tpi.service.TramoService;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tramos")
@Tag(
    name = "Tramos",
    description = "Operaciones CRUD para la gestión de tramos en el sistema"
)
public class TramoController {

    private final TramoService tramoService;


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
        return tramoService.findAll();
    }


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
        return tramoService.save(tramo);
    }


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
        return tramoService.getById(id);
    }

    /**
     * Asignar camion a tramo
     * @param tramoId
     * @param camionId
     * @return
     */
    @Operation(
        summary = "Asigna un camión a un tramo",
        description = "Permite asignar un camión existente a un tramo específico. "
                    + "Si el tramo o el camión no existen, se devuelve un error correspondiente."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Camión asignado correctamente al tramo",
            content = @Content(schema = @Schema(implementation = TramoConDetalles.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "El tramo o el camión no existen"
        )
    })
    @PatchMapping("/{id}/asignar-camion/{camionId}")
    public TramoConDetalles asignarCamion(
            @Parameter(description = "ID del tramo al que se le asignará el camión", example = "12")
            @PathVariable("id") Long tramoId,

            @Parameter(description = "ID del camión que será asignado al tramo", example = "5")
            @PathVariable("camionId") Long camionId
    ) {
        return tramoService.asignarCamionATramo(tramoId, camionId);
    }


    @PatchMapping("/{id}/estado/{estadoId}")
    public TramoConDetalles actualizarEstadoTramo(
            @PathVariable Long id,
            @PathVariable Long estadoId
    ) {
        // Llamás a tu servicio para actualizar el estado del tramo
        return tramoService.actualizarEstado(id, estadoId);
    }

}



