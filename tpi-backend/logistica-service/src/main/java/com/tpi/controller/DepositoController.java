package com.tpi.controller;

import com.tpi.dto.request.ActualizarDepositoRequest;
import com.tpi.dto.request.DepositoRequest;
import com.tpi.model.Deposito;
import com.tpi.service.DepositoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/depositos")
@Tag(
    name = "Depósitos",
    description = "Operaciones para la gestión de depósitos en el sistema logístico"
)
@Tag(
    name = "Depósitos",
    description = "Operaciones para la gestión de depósitos en el sistema logístico"
)
public class DepositoController {

    private final DepositoService depositoService;

    @Operation(
        summary = "Obtener todos los depósitos",
        description = "Retorna una lista completa de todos los depósitos registrados en el sistema."
    )
    @GetMapping
    public List<Deposito> listarDepositos() {
        return depositoService.findAll();
    }

    @Operation(
        summary = "Crear un nuevo depósito",
        description = "Registra un nuevo depósito en el sistema con la información proporcionada."
    )
    @PostMapping
    public ResponseEntity<Deposito> crearDeposito(
            @Parameter(description = "Datos del depósito a crear")
            @RequestBody @Valid DepositoRequest request
    ) {

        Deposito deposito = depositoService.crearDeposito(request);

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201
                .body(deposito);
    }

    

    @Operation(
        summary = "Obtener depósito por ID", 
        description = "Busca un depósito específico utilizando su identificador único."
    )
    @GetMapping("/{id}")
    public Deposito obtenerPorId(
        @Parameter(description = "ID único del depósito", example = "1")
        @PathVariable Long id
    ) {
        return depositoService.findById(id);
    }

    @Operation(
        summary = "Actualizar depósito parcialmente",
        description = "Actualiza campos específicos de un depósito existente. Solo los campos no nulos serán actualizados.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Depósito actualizado exitosamente",
                content = @Content(schema = @Schema(implementation = Deposito.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Depósito no encontrado"
            )
        }
    )
    @PatchMapping("/{id}")
    public Deposito actualizarDeposito(
        @Parameter(description = "ID del depósito a actualizar", required = true)
        @PathVariable Long id,
        @Parameter(
            description = "Campos a actualizar",
            examples = @ExampleObject(
                name = "Actualizar parcial",
                value = """
                    {
                      "nombre": "Depósito Norte Actualizado",
                      "costoEstadiaPorDia": 2500.75
                    }
                    """
            )
        )
        @RequestBody @Valid ActualizarDepositoRequest request
    ) {
        return depositoService.actualizarDeposito(id, request);
    }

    @Operation(
        summary = "Eliminar depósito",
        description = "Elimina permanentemente un depósito del sistema",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Depósito eliminado exitosamente"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Depósito no encontrado"
            )
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDeposito(
        @Parameter(description = "ID del depósito a eliminar", required = true)
        @PathVariable Long id
    ) {
        depositoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

