package com.tpi.controller;

import com.tpi.dto.request.UbicacionRequestDTO;
import com.tpi.dto.response.UbicacionDTOs.UbicacionResponseDTO;
import com.tpi.model.Ubicacion;
import com.tpi.service.UbicacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ubicaciones")
@Tag(
    name = "Ubicaciones",
    description = "Operaciones CRUD para la gestión de ubicaciones en el sistema"
)
public class UbicacionController {

    private final UbicacionService ubicacionService;

    /**
     * Crea nueva ubicacion.
     */
    @Operation(
        summary = "Crear nueva ubicación",
        description = "Registra una nueva ubicación en el sistema con la información proporcionada."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ubicación creada exitosamente",
            content = @Content(schema = @Schema(implementation = UbicacionResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        )
    })
    @PostMapping
    public UbicacionResponseDTO crearUbicacion(
        @Parameter(description = "Datos de la ubicación a crear")
        @Valid @RequestBody UbicacionRequestDTO request) {
            
        Ubicacion ubicacion = ubicacionService.crearUbicacion(request);
        
        return UbicacionResponseDTO.fromEntity(ubicacion);
    }




    /**
     * Lista solicitudes
     */
    @Operation(
        summary = "Listar todas las ubicaciones",
        description = "Obtiene una lista completa de todas las ubicaciones registradas en el sistema."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de ubicaciones obtenida exitosamente",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = UbicacionResponseDTO.class)))
    )
    @GetMapping
    public List<UbicacionResponseDTO> listarUbicaciones() {

        return ubicacionService.obtenerTodas();
    }

    @Operation(
        summary = "Obtener ubicación por ID",
        description = "Busca una ubicación específica utilizando su identificador único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ubicación encontrada",
            content = @Content(schema = @Schema(implementation = UbicacionResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ubicación no encontrada"
        )
    })
    @GetMapping("/{id}")
    public UbicacionResponseDTO obtenerPorId(
        @Parameter(description = "ID único de la ubicación", example = "1")
        @PathVariable Long id) {
        Ubicacion ubicacion = ubicacionService.findById(id);
        return UbicacionResponseDTO.fromEntity(ubicacion);
    }

    @Operation(
        summary = "Actualizar ubicación completamente",
        description = "Actualiza todos los campos de una ubicación existente."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ubicación actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = UbicacionResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ubicación no encontrada"
        )
    })
    @PutMapping("/{id}")
    public UbicacionResponseDTO actualizarUbicacion(
        @Parameter(description = "ID de la ubicación a actualizar")
        @PathVariable Long id,
        @Parameter(description = "Datos actualizados de la ubicación")
        @Valid @RequestBody UbicacionRequestDTO request) {
        Ubicacion ubicacion = ubicacionService.actualizarUbicacion(id, request);
        return UbicacionResponseDTO.fromEntity(ubicacion);
    }

    @Operation(
        summary = "Actualizar ubicación parcialmente",
        description = "Actualiza campos específicos de una ubicación existente. Solo los campos no nulos serán actualizados."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ubicación actualizada parcialmente exitosamente",
            content = @Content(schema = @Schema(implementation = UbicacionResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ubicación no encontrada"
        )
    })
    @PatchMapping("/{id}")
    public UbicacionResponseDTO actualizarParcialUbicacion(
        @Parameter(description = "ID de la ubicación a actualizar")
        @PathVariable Long id,
        @Parameter(
            description = "Campos a actualizar",
            examples = @ExampleObject(
                name = "Actualizar parcial",
                value = """
                    {
                      "direccion": "Av. Siempre Viva 123 Actualizada",
                      "latitud": -34.603722,
                      "longitud": -58.381592
                    }
                    """
            )
        )
        @RequestBody Map<String, Object> updates) {
        Ubicacion ubicacion = ubicacionService.actualizarParcialUbicacion(id, updates);
        return UbicacionResponseDTO.fromEntity(ubicacion);
    }

    @Operation(
        summary = "Eliminar ubicación",
        description = "Elimina permanentemente una ubicación del sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Ubicación eliminada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ubicación no encontrada"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUbicacion(
        @Parameter(description = "ID de la ubicación a eliminar")
        @PathVariable Long id) {
        ubicacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}