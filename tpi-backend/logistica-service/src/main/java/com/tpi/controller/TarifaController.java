package com.tpi.controller;

import com.tpi.dto.request.ActualizarTarifaRequest;
import com.tpi.model.Tarifa;
import com.tpi.service.TarifaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tarifas")
@Tag(
    name = "Tarifas",
    description = "Operaciones para la gestión de tarifas en el sistema"
)
public class TarifaController {

    private final TarifaService tarifaService;

    @Operation(
        summary = "Obtener todas las tarifas",
        description = "Retorna una lista completa de todas las tarifas registradas en el sistema."
    )
    @GetMapping
    public List<Tarifa> listarTarifas() {
        return tarifaService.findAll();
    }

    @Operation(
        summary = "Obtener tarifa por ID",
        description = "Busca una tarifa específica utilizando su identificador único."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarifa encontrada"),
        @ApiResponse(responseCode = "404", description = "Tarifa no encontrada")
    })
    @GetMapping("/{id}")
    public Tarifa obtenerPorId(
        @Parameter(description = "ID único de la tarifa", example = "1")
        @PathVariable Long id
    ) {
        return tarifaService.findById(id);
    }

    @Operation(
        summary = "Buscar tarifa por nombre",
        description = "Busca una tarifa por su nombre exacto."
    )
    @GetMapping("/buscar")
    public ResponseEntity<Tarifa> buscarPorNombre(
        @Parameter(description = "Nombre de la tarifa", example = "ESTANDAR")
        @RequestParam String nombre
    ) {
        return tarifaService.findByNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Crear nueva tarifa",
        description = "Registra una nueva tarifa en el sistema."
    )
    @PostMapping
    public Tarifa crearTarifa(
        @Parameter(description = "Datos de la tarifa a crear")
        @RequestBody @Valid Tarifa tarifa
    ) {
        return tarifaService.save(tarifa);
    }

    @Operation(
        summary = "Actualizar tarifa parcialmente",
        description = "Actualiza campos específicos de una tarifa existente. Solo los campos no nulos serán actualizados.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tarifa actualizada exitosamente",
                content = @Content(schema = @Schema(implementation = Tarifa.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tarifa no encontrada"
            )
        }
    )
    @PatchMapping("/{id}")
    public Tarifa actualizarTarifa(
        @Parameter(description = "ID de la tarifa a actualizar", required = true)
        @PathVariable Long id,
        @Parameter(
            description = "Campos a actualizar",
            examples = @ExampleObject(
                name = "Actualizar parcial",
                value = """
                    {
                      "nombre": "Tarifa Mediano Actualizada",
                      "costoPorKmBase": 180.50,
                      "precioCombustiblePorLitro": 195.75
                    }
                    """
            )
        )
        @RequestBody @Valid ActualizarTarifaRequest request
    ) {
        return tarifaService.actualizarTarifa(id, request);
    }

    @Operation(
        summary = "Eliminar tarifa",
        description = "Elimina permanentemente una tarifa del sistema",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Tarifa eliminada exitosamente"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tarifa no encontrada"
            )
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarifa(
        @Parameter(description = "ID de la tarifa a eliminar", required = true)
        @PathVariable Long id
    ) {
        tarifaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}