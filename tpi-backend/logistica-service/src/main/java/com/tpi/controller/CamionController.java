package com.tpi.controller;

import com.tpi.dto.request.ActualizarCamionRequest;
import com.tpi.model.Camion;
import com.tpi.service.CamionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/camiones")
@Tag(
    name = "Gestión de Camiones",
    description = "Operaciones CRUD para la gestión de camiones en el sistema de logística"
)
public class CamionController {

    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @Operation(
        summary = "Obtener todos los camiones",
        description = """
            Retorna una lista completa de todos los camiones registrados en el sistema.
            Incluye información como patente, marca, modelo, capacidad de carga y estado.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de camiones obtenida exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Camion.class))
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    @GetMapping
    public List<Camion> listarCamiones() {
        return camionService.findAll();
    }

    @Operation(
        summary = "Crear un nuevo camión",
        description = """
            Registra un nuevo camión en el sistema de logística.
            Requiere todos los datos básicos del camión para su creación.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Camión creado exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Camion.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )

    @PostMapping
    public Camion crearCamion(
        @Parameter(
            description = "Datos del camión a crear",
            required = true,
            examples = @ExampleObject(
                name = "Camión ejemplo",
                summary = "Ejemplo de creación de camión",
                value = """
                    {
                      "patente": "ABC123",
                      "marca": "Volvo",
                      "modelo": "FH16",
                      "capacidadCarga": 25000.5,
                      "estado": "DISPONIBLE"
                    }
                    """
            )
        )
        @RequestBody @Valid Camion camion
    ) {
        return camionService.save(camion);
    }

    /**
     * Get camion by  id
     */
    @Operation(
        summary = "Obtener camión por ID",
        description = """
            Busca y retorna un camión específico utilizando su identificador único.
            Si el camión no existe, retorna un error 404.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Camión encontrado exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Camion.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Camión no encontrado"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    @GetMapping("/{id}")
    public Camion obtenerPorId(
        @Parameter(
            description = "ID único del camión",
            required = true,
            example = "1"
        )
        @PathVariable Long id
    ) {
        return camionService.findById(id);
    }

    /**
     * Actualizar camion Patch
     * @param id
     * @param request
     * @return
     */
    @Operation(
        summary = "Actualizar camión parcialmente",
        description = """
            Actualiza los campos específicos de un camión existente.
            Solo los campos no nulos serán actualizados.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Camión actualizado exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Camion.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Camión no encontrado"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    @PatchMapping("/{id}")
    public Camion actualizarCamion(
        @Parameter(description = "ID del camión a actualizar", required = true)
        @PathVariable Long id,
        
        @Parameter(
            description = "Campos a actualizar (solo los enviados se modificarán)",
            required = true,
            examples = @ExampleObject(
                name = "Actualizar parcial",
                summary = "Ejemplo actualizando solo conductor y costo",
                value = """
                    {
                    "nombreConductor": "Carlos López",
                    "telefonoConductor": "+54 11 9876-5432",
                    "costoPorKm": 150.50
                    }
                    """
            )
        )
        @RequestBody @Valid ActualizarCamionRequest request
    ) {
        return camionService.actualizarCamion(id, request);
    }

    /**
     * Dletea por id
     * @param id
     * @return
     */
    @Operation(
        summary = "Eliminar camión",
        description = "Elimina permanentemente un camión del sistema",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Camión eliminado exitosamente"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Camión no encontrado"
            ),
            @ApiResponse(
                responseCode = "500", 
                description = "Error interno del servidor"
            )
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCamion(
        @Parameter(description = "ID del camión a eliminar", required = true)
        @PathVariable Long id
    ) {
        camionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
