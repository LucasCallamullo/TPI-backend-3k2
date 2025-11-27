package com.tpi.controller;

import com.tpi.dto.CostoFinalDTOs.CostoFinalDTO;
import com.tpi.dto.request.CrearRutaCompletaRequest;
import com.tpi.dto.response.CostosEstimadosDTOs.CostosEstimadosDTO;
import com.tpi.dto.response.RutaAsignadaResponseDTO;
import com.tpi.dto.response.RutasTramosCamionResponsesDTO.RutaTramosCamionResponse;
import com.tpi.model.Ruta;
import com.tpi.service.RutaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rutas")
@Tag(name = "Rutas", description = "API para gestión de rutas de transporte")
public class RutaController {

    private final RutaService rutaService;

    /**
     * Este endpoint se llama desde el ms-solicitudes es para asignarle una ruta
     */
    @Operation(
        summary = "Crear ruta para solicitud",
        description = "Crea una ruta completa con tramos automáticos para una solicitud de transporte"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Ruta creada exitosamente",
            content = @Content(schema = @Schema(implementation = RutaAsignadaResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Ubicación, tarifa o depósitos no encontrados"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor"
        )
    })
    @PostMapping("/asignar-a-solicitud")
    public ResponseEntity<RutaAsignadaResponseDTO> crearRutaParaSolicitud(
            @Parameter(description = "Datos para crear la ruta completa", required = true)
            @RequestBody CrearRutaCompletaRequest request) {
        
        RutaAsignadaResponseDTO rutaInfo = rutaService.crearRutaParaSolicitud(request);
        return ResponseEntity.ok(rutaInfo);
    }

    /**
     * CALCULAR COSTOS ESTIMADOS PARA SOLICITUD
     * 
     * Este endpoint calcula los costos y gastos estimados de logística para una solicitud específica,
     * incluyendo transporte, combustibles, peajes, y otros gastos asociados a la ruta asignada.
     * 
     * El cálculo se basa en la ruta asignada a la solicitud y las tarifas configuradas en el sistema.
     */
    @Operation(
        summary = "Calcular costos estimados para solicitud",
        description = """
            Calcula los costos y gastos estimados de logística para una solicitud específica.
            
            **Proceso de cálculo:**
            - Recupera la ruta asignada a la solicitud
            - Obtiene los tramos y distancias de la ruta
            - Aplica las tarifas configuradas según el tipo de carga
            - Calcula costos de combustible, peajes, y gastos operativos
            - Retorna el desglose detallado de todos los costos estimados
            
            **Nota:** Este endpoint es de solo lectura y no modifica el estado de la solicitud.
            """,
        tags = {"Rutas", "Costos"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Cálculo de costos estimados realizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostosEstimadosDTO.class)
                
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Solicitud no encontrada o sin ruta asignada",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                        "timestamp": "2024-01-15T10:30:00Z",
                        "message": "No se encontró una ruta asignada para la solicitud 1",
                        "code": "SOLICITUD_SIN_RUTA"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "ID de solicitud inválido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                        "timestamp": "2024-01-15T10:30:00Z",
                        "message": "El ID de solicitud debe ser mayor a 0",
                        "code": "ID_INVALIDO"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor durante el cálculo de costos",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                        "timestamp": "2024-01-15T10:30:00Z",
                        "message": "Error al acceder a la base de datos de tarifas",
                        "code": "ERROR_CALCULO_COSTOS"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/solicitud/{solicitudId}/calcular-costos-estimados")
    public CostosEstimadosDTO calcularGastosEstimados(
        @Parameter(
            description = "ID único de la solicitud de logística",
            example = "12345",
            required = true
        )
        @PathVariable Long solicitudId) {
        
        return rutaService.calcularGastosEstimados(solicitudId);
    }

    
    /**
     * Calcular costos totales para una solicitud
     */
    @Operation(
        summary = "Calcular costos totales de la solicitud",
        description = "Calcula los costos totales finales de logística para una solicitud específica, " +
                    "incluyendo transporte, combustibles, peajes, seguros e impuestos.",
        tags = {"Costos", "Solicitudes"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Costos totales calculados exitosamente",
            content = @Content(schema = @Schema(implementation = CostoFinalDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solicitud no encontrada"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "ID de solicitud inválido"
        )
    })
    @GetMapping("/solicitud/{solicitudId}/calcular-costos-totales")
    public CostoFinalDTO calcularGastosTotales(
        @Parameter(description = "ID de la solicitud", example = "12345")
        @PathVariable Long solicitudId) {
        
        return rutaService.calcularGastosTotales(solicitudId);
    }



    /**
     * Info extra de todo lo respecto a la solicitud del cliente
     */
    @Operation(
        summary = "Obtener ruta por ID de solicitud",
        description = "Recupera la ruta completa con todos sus tramos asociados a una solicitud específica"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ruta encontrada exitosamente",
            content = @Content(schema = @Schema(implementation = RutaTramosCamionResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "No se encontró ruta para la solicitud especificada"
        )
    })
    @GetMapping("/solicitud/{solicitudId}")
    public RutaTramosCamionResponse obtenerRutaPorSolicitudId(
        @Parameter(description = "ID de la solicitud", example = "1")
        @PathVariable Long solicitudId) {
        return rutaService.obtenerRutaConTramosPorSolicitudId(solicitudId);
    }


    



    /**
     * Lista todas las rutas GET all
     */
    @Operation(
        summary = "Listar todas las rutas",
        description = "Obtiene un listado de todas las rutas disponibles en el sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de rutas obtenida exitosamente",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Ruta.class)))
    )
    @GetMapping
    public List<Ruta> listarRutas() {
        return rutaService.findAll();
    }

    /**
     * Crea ruta concepto basico 
     * @param ruta
     * @return
     */
    @Operation(
        summary = "Crear una ruta básica",
        description = "Crea una ruta básica sin tramos automáticos (uso administrativo)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ruta creada exitosamente",
            content = @Content(schema = @Schema(implementation = Ruta.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de la ruta inválidos"
        )
    })
    @PostMapping
    public Ruta crearRuta(
            @Parameter(description = "Objeto Ruta a crear", required = true)
            @RequestBody Ruta ruta) {
        return rutaService.save(ruta);
    }

    /**
     * Get by id alguna ruta
     */
    @Operation(
        summary = "Obtener ruta por ID",
        description = "Recupera una ruta específica por su identificador único"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ruta encontrada",
            content = @Content(schema = @Schema(implementation = Ruta.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ruta no encontrada"
        )
    })
    @GetMapping("/{id}")
    public Ruta obtenerPorId(
            @Parameter(description = "ID de la ruta a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return rutaService.findById(id);
    }
}
