package com.tpi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import com.tpi.dto.external.CostoFinalDTO;
import com.tpi.dto.external.CostosEstimadosDTO;
import com.tpi.dto.request.AsignarRutaRequest;
import com.tpi.dto.request.SolicitudCompletaRequestDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudWithRutaResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudWithUbicacionAndRutaResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudWithUbicacionResponseDTO;
import com.tpi.dto.response.ContenedorResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudResponseDTO;

import com.tpi.service.SolicitudService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.List;

/**
 * Controller para gestionar las operaciones relacionadas con solicitudes de transporte
 * Expone endpoints REST para crear, consultar y actualizar solicitudes
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/solicitudes")
@Tag(name = "Solicitudes", description = "API para gestión de solicitudes de transporte")
public class SolicitudController {
    
    private final SolicitudService solicitudService;
    
    /**
     * GET ALL - Consultar todas las solicitudes
     * Retorna lista completa de solicitudes (para Operador/Administrador)
     */
    @Operation(
        summary = "Obtener todas las solicitudes",
        description = """
            Retorna lista completa de solicitudes con posibilidad de filtrado.
            
            Roles permitidos:
            • OPERADOR
            • ADMINISTRADOR
            
            Filtros disponibles:
            • estado: PENDIENTE, PROGRAMADA, EN_CURSO, COMPLETADA, CANCELADA
            
            Ejemplo: /api/v1/solicitudes?estado=PENDIENTE
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Lista de solicitudes obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = SolicitudResponseDTO[].class))
        ),
        @ApiResponse(
            responseCode = "404", description = "Contenedor de la solicitud no encontrado"
        )
    })
    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> obtenerTodasSolicitudes(
            @Parameter(description = "Filtrar solicitudes por estado")
            @RequestParam(required = false) String estado
        ) {
        
        List<SolicitudResponseDTO> response = solicitudService.findAll(estado);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para solicitar contenedor asociado como dto a devolver
     * @param id es el ID de la socicitud que tiene su contenedor asociado
     * @return
     */
    @Operation(
        summary = "Obtener contenedor de una solicitud",
        description = "Devuelve la información del contenedor asociado a una solicitud específica por su ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Contenedor obtenido correctamente",
            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContenedorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Solicitud no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    @GetMapping("/{id}/contenedor")
    public ResponseEntity<ContenedorResponseDTO> consultarSolicitudes(
        @Parameter(description = "ID de la solicitud", required = true, example = "1")
        @PathVariable Long id
    ) {  
        ContenedorResponseDTO response = solicitudService.obtenerContenedor(id);
        return ResponseEntity.ok(response);
    }


    /**
     * GET - Consultar solicitud por ID
     * Retorna todos los datos de una solicitud específica
     */
    @Operation(
        summary = "Obtener solicitud por ID",
        description = "Recupera la información completa de una solicitud específica mediante su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Solicitud encontrada",
            content = @Content(schema = @Schema(implementation = SolicitudResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", description = "Solicitud no encontrada"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> obtenerSolicitud(
            @Parameter(
                description = "ID de la solicitud",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
                
        SolicitudResponseDTO response = solicitudService.getDTOById(id);
        return ResponseEntity.ok(response);
    }


    /**
     * PATCH - Actualizar estado de una solicitud
     * URL más RESTful con parámetro de query
     * PATCH /solicitudes/123/estado?estado=PROGRAMADA
     */
    @Operation(
        summary = "Actualizar estado de una solicitud",
        description = """
            Actualiza el estado de una solicitud específica. 
            • Estados válidos: PENDIENTE, PROGRAMADA, EN_CURSO, COMPLETADA, CANCELADA
        """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Estado de la solicitud actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = SolicitudResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solicitud no encontrada"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Estado inválido o no permitido"
        )
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponseDTO> actualizarEstadoSolicitud(
            @Parameter(
                description = "ID de la solicitud",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @Parameter(
                description = "Nuevo estado de la solicitud",
                examples = {
                    @ExampleObject(name = "Pendiente", value = "PENDIENTE"),
                    @ExampleObject(name = "Programada", value = "PROGRAMADA"),
                    @ExampleObject(name = "En curso", value = "EN_CURSO"),
                    @ExampleObject(name = "Completada", value = "COMPLETADA"),
                    @ExampleObject(name = "Cancelada", value = "CANCELADA")
                },
                required = true
            )
            @RequestParam String estado) {
        
        SolicitudResponseDTO response = solicitudService.actualizarEstado(id, estado);
        return ResponseEntity.ok(response);
    }

    /**
     * POST - Registrar nueva solicitud de transporte completa
     * Recibe todos los datos necesarios y orquesta la creación en ambos microservicios
     * Incluye datos del contenedor, ubicación de origen y ubicación de destino
     */
    @Operation(
        summary = "Crear nueva solicitud de transporte",
        description = """
            Registra una nueva solicitud de transporte completa. 
            Incluye datos del contenedor, ubicación de origen y destino. 
            Orquesta la creación en ambos microservicios
        """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201", description = "Solicitud creada exitosamente",
            content = @Content(schema = @Schema(implementation = SolicitudWithUbicacionResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", description = "Datos de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "409", description = "Conflicto - Contenedor ya existe o datos duplicados"
        )
    })
    @PostMapping
    public ResponseEntity<SolicitudWithUbicacionResponseDTO> crearSolicitud(
        @Parameter(
            description = "Datos completos para crear la solicitud", required = true,
            schema = @Schema(implementation = SolicitudCompletaRequestDTO.class)
        )
        @Valid @RequestBody SolicitudCompletaRequestDTO request,
        @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {

        // Aprovechamos el jwt para que el cliente tenga asociada una solicitud
        // entendimos que el cliente era quien hacía la solicitud
        String keycloakId = jwt.getSubject();
        log.info("Creando solicitud para cliente Keycloak ID: {}", keycloakId);
        
        SolicitudWithUbicacionResponseDTO response = solicitudService.crearSolicitudCompleta(request, keycloakId);
        
        log.info("Solicitud creada exitosamente ID: {} para cliente: {}", 
                response.id(), keycloakId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Endpoint utilizado para recibir info completa de la solicitud, con su ubicacion y su ruta y tramos
     */
    @Operation(
        summary = "Seguimiento de solicitud",
        description = "Permite al cliente consultar el estado actual del traslado de su contenedor"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Seguimiento obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = SolicitudWithUbicacionAndRutaResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Solicitud no encontrada"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No tiene permisos para ver esta solicitud"
        )
    })
    @GetMapping("/seguimiento/{solicitudId}")
    public SolicitudWithUbicacionAndRutaResponseDTO obtenerSeguimiento(
        @Parameter(description = "ID de la solicitud") 
        @PathVariable Long solicitudId) {
        
        return solicitudService.seguimientoSolicitud(solicitudId);
    }


    /*
    * Asigna ruta a una solicitud creada
    */
    @Operation(
        summary = "Asignar ruta a solicitud",
        description = """
            Asigna una ruta con sus tramos a una solicitud de transporte y retorna la solicitud actualizada con la ruta asignada.
            
            **Permisos requeridos:** ROLE_OPERADOR o ROLE_ADMIN
            
            **Flujo:**
            1. Valida que la solicitud exista y esté en estado PROGRAMABLE
            2. Crea la ruta con los tramos especificados en MS-LOGISTICA
            3. Actualiza el estado de la solicitud a PROGRAMADA
            4. Retorna la solicitud actualizada con los detalles completos de la ruta asignada
            
            **Estados válidos para asignar ruta:** BORRADOR, PROGRAMABLE
            
            **Validaciones de tramos:**
            - El primer tramo debe comenzar en el origen de la solicitud
            - El último tramo debe terminar en el destino de la solicitud  
            - Los tramos deben ser secuenciales y continuos (destino = origen del siguiente)
            - El orden debe ser secuencial empezando desde 1
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ruta asignada exitosamente - Retorna la solicitud actualizada con los detalles de la ruta",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SolicitudWithRutaResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida - Puede ser: ID inválido, datos de ruta incompletos, solicitud no está en estado programable, tramos mal formados o no secuenciales",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solicitud no encontrada - Verifique que el ID de solicitud exista",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No autorizado - Se requiere rol OPERADOR o ADMIN",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto - La solicitud ya tiene una ruta asignada o no está en estado programable",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Servicio no disponible - MS-LOGISTICA no está disponible para crear la ruta",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping("/{id}/asignar-ruta")
    public ResponseEntity<SolicitudWithRutaResponseDTO> asignarRuta(
        @Parameter(
            description = "ID de la solicitud a la que se asignará la ruta",
            required = true, example = "1"
        )
        @PathVariable Long id,
        
        @Parameter(
            description = """
                Datos de la ruta y tramos a asignar. Los tramos deben ser secuenciales y 
            formar una ruta continua desde el origen hasta el destino de la solicitud.
            """,
            required = true
        )
        @RequestBody @Valid AsignarRutaRequest request) {
        
        SolicitudWithRutaResponseDTO solicitudActualizada = solicitudService.asignarRuta(id, request);
        return ResponseEntity.ok(solicitudActualizada);
    }

    /**
     * CALCULAR COSTOS ESTIMADOS PARA SOLICITUD
     * 
     * Este endpoint calcula los costos estimados de logística para una solicitud específica
     * y actualiza la solicitud con la información de costos calculada.
     * 
     * @param solicitudId ID de la solicitud para la cual se calcularán los costos estimados
     * @return CostosEstimadosDTO con el desglose detallado de costos calculados
     */
    @Operation(
        summary = "Calcular costos estimados para solicitud",
        description = """
            Calcula los costos estimados de logística (transporte, manejo, seguros, etc.) 
            para una solicitud específica y actualiza la solicitud con esta información.
            
            Flujo del proceso:
            1. Valida la existencia de la solicitud
            2. Consulta al microservicio de logística para cálculo de costos
            3. Actualiza la solicitud con los costos estimados
            4. Retorna el desglose detallado de costos
            """,
        tags = {"Solicitudes - Costos"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Costos estimados calculados exitosamente",
            content = @Content(schema = @Schema(implementation = CostosEstimadosDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Solicitud no encontrada"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de solicitud inválidos para cálculo de costos"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor durante el cálculo"
        )
    })
    @PatchMapping("/{id}/calcular-costos-estimados")
    public ResponseEntity<CostosEstimadosDTO> calcularCostosEstimados(
        @Parameter(description = "ID de la solicitud", example = "12345")
        @PathVariable("id") Long solicitudId) {
        
        CostosEstimadosDTO costos = solicitudService.calcularCostosEstimados(solicitudId);
        return ResponseEntity.ok(costos);
    }


    /**
     * CALCULAR Y ACTUALIZAR COSTOS TOTALES DE SOLICITUD
     * 
     * Este endpoint calcula los costos totales finales para una solicitud y actualiza
     * el registro de la solicitud con los valores calculados. La operación es de tipo
     * PATCH ya que modifica parcialmente el recurso solicitud.
     */
    @Operation(
        summary = "Calcular y actualizar costos totales de solicitud",
        description = """
            Calcula los costos totales finales de la operación logística y actualiza la solicitud
            con los valores calculados. Esta operación modifica el estado de la solicitud.
            
            **Características de la operación:**
            - **Tipo:** PATCH - Actualización parcial del recurso
            - **Idempotente:** Sí - Múltiples llamadas con los mismos datos producen el mismo resultado
            - **Side Effects:** Actualiza el campo de costos en la solicitud
            
            **Proceso ejecutado:**
            1. Validación de existencia de la solicitud
            2. Cálculo de costos totales (transporte, combustibles, peajes, impuestos)
            3. Actualización del campo de costos en la entidad Solicitud
            4. Persistencia de los cambios en base de datos
            5. Retorno del detalle de costos calculados
            
            """,
        tags = {"Solicitudes", "Costos", "Actualizaciones"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Costos totales calculados y solicitud actualizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostoFinalDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida para cálculo de costos",
            content = @Content(
                mediaType = "application/json"
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Solicitud no encontrada",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "noEncontrada",
                    summary = "Solicitud no existe",
                    value = """
                        {
                        "timestamp": "2024-01-15T14:30:00Z",
                        "message": "No se encontró la solicitud con ID 99999",
                        "code": "SOLICITUD_NO_ENCONTRADA",
                        "path": "/api/solicitudes/99999/calcular-costos-totales"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "errorInterno",
                    summary = "Error inesperado",
                    value = """
                        {
                        "timestamp": "2024-01-15T14:30:00Z",
                        "message": "Error interno durante el cálculo de costos",
                        "code": "ERROR_INTERNO",
                        "detalle": "Timeout en conexión a base de datos"
                        }
                        """
                )
            )
        )
    })
    @PatchMapping("/{id}/calcular-costos-totales")
    public ResponseEntity<CostoFinalDTO> calcularCostosTotales(
        @Parameter(
            description = "ID único de la solicitud a actualizar",
            example = "12345",
            required = true,
            schema = @Schema(
                minimum = "1",
                type = "integer",
                description = "Identificador único de la solicitud"
            )
        )
        @PathVariable("id") Long solicitudId) {
        
        log.info("[PATCH] Calculando y actualizando costos totales para solicitud ID: {}", solicitudId);
        
        CostoFinalDTO costos = solicitudService.calcularCostosTotales(solicitudId);
    
        return ResponseEntity.ok(costos);
    }

}
