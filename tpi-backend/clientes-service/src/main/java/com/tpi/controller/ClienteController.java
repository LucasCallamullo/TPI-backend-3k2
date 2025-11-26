package com.tpi.controller;

import com.tpi.dto.ActualizarClienteRequest;
import com.tpi.dto.responses.ClienteDTO;
import com.tpi.dto.SincronizarClienteRequest;
import com.tpi.dto.request.ClienteRequest;
import com.tpi.model.Cliente;
import com.tpi.service.ClienteService;
import com.tpi.service.KeycloakService;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.tpi.service.SecurityContextService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "API para gestión de clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final SecurityContextService securityContextService;
    private final KeycloakService keycloakService;

    /* crear clientes */
    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<ClienteDTO> registrarCliente(@RequestBody @Valid ClienteRequest request) {

        // 1. Crear usuario en Keycloak y obtener ID
        String keycloakId = keycloakService.registrarUsuarioEnKeycloak(request);

        // 2. Crear cliente en tu base local
        Cliente cliente = clienteService.crearClienteDesdeRegistro(request, keycloakId);
        ClienteDTO clienteDTO = ClienteDTO.fromEntity(cliente);

        // 3. Responder 201 Created
        URI location = URI.create("/clientes/" + cliente.getId());

        return ResponseEntity
                .created(location)
                .body(clienteDTO);
    }

    /*
     * Sincroniza la base de datos con el keycloackID para info extra de nosotros 
     */
    @Operation(
        summary = "Sincronizar cliente desde Keycloak",
        description = "Crea o actualiza un cliente en base a la información proveniente de Keycloak"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Cliente sincronizado exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos - Puede ser: JSON mal formado, campos requeridos faltantes, formato de email inválido",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @PostMapping("/sincronizar")
    public ResponseEntity<?> sincronizarCliente(
            @Valid @RequestBody SincronizarClienteRequest request) {
        
        try {
            System.out.println("Received sync request for: " + request.getKeycloakId());
            
            // Llamar al servicio para sincronizar el cliente
            Cliente cliente = clienteService.sincronizarCliente(
                request.getKeycloakId(),
                request.getNombre(),
                request.getEmail(),
                request.getTelefono(),
                request.getDireccion()
            );
            
            // Construir respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Cliente sincronizado exitosamente");
            response.put("clienteId", cliente.getId());
            response.put("nombre", cliente.getNombre());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Manejo de errores con logging
            System.err.println("Error sincronizando cliente: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Actualiza cliente a si mismo
     */
    @Operation(
        summary = "Actualizar datos del cliente autenticado",
        description = """
            Permite al cliente actualizar parcialmente sus datos personales.
            
            **Características:**
            - Actualización parcial: solo los campos enviados se actualizan
            - Identificación automática mediante JWT
            - Solo permite actualizar el propio perfil
            - Campos opcionales: nombre, email, teléfono, dirección
            
            **Comportamiento:**
            - Campos nulos o no enviados se ignoran
            - Solo se realiza UPDATE si hay cambios reales
            - Si el cliente no existe en BD, se crea automáticamente
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Cliente actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Cliente.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Request mal formado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado o token JWT inválido",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PatchMapping("/me")
    public Cliente actualizarMisDatos(
        @Parameter(
            description = "DTO con los campos a actualizar. Solo los campos no nulos serán considerados.",
            required = true,
            examples = {
                @ExampleObject(
                    name = "Actualizar solo teléfono",
                    summary = "Ejemplo actualizando solo teléfono",
                    value = """
                        {
                        "telefono": "+54 11 1234-5678"
                        }
                        """
                ),
                @ExampleObject(
                    name = "Actualizar múltiples campos", 
                    summary = "Ejemplo actualizando nombre y dirección",
                    value = """
                        {
                        "nombre": "Juan Carlos Pérez",
                        "direccion": "Av. Siempre Viva 123, Springfield"
                        }
                        """
                )
            }
        )
        @RequestBody @Valid ActualizarClienteRequest request) {
        
        String keycloakId = securityContextService.obtenerClienteIdDesdeToken();
        return clienteService.sincronizarCliente(
            keycloakId, 
            request.nombre(),
            request.email(), 
            request.telefono(),
            request.direccion()
        );
    }

    /*
     * Obtener Cliente por ID
     */
    @Operation(
        summary = "Obtener cliente por ID",
        description = "Recupera la información de un cliente específico mediante su ID de Keycloak"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = Cliente.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Cliente no encontrado - Verifique que el ID de Keycloak exista"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getCliente(
        @Parameter(
            description = "ID único del usuario en Keycloak (UUID)",
            example = "123e4567-e89b-12d3-a456-426614174000",
            required = true
        )
        @PathVariable String id) {

        ClienteDTO dto = clienteService.getDTOClienteById(id);
        return ResponseEntity.ok(dto);
    }

    /*
     * Obtener Clientes listados
     */
    @Operation(
        summary = "Listar todos los clientes",
        description = "Obtiene una lista de todos los clientes registrados en el sistema"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Lista de clientes obtenida exitosamente",
        content = @Content(schema = @Schema(implementation = Cliente[].class))
    )
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.getAllClientes();
    }

}