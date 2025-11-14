package com.tpi.controller;

import com.tpi.dto.SincronizarClienteRequest;
import com.tpi.model.Cliente;
import com.tpi.service.ClienteService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "API para gestión de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    // Inyección de dependencias del servicio mediante constructor
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

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
                request.getTelefono()
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
    public ResponseEntity<?> getCliente(
            @Parameter(
                description = "ID único del usuario en Keycloak (UUID)",
                example = "123e4567-e89b-12d3-a456-426614174000",
                required = true
            )
            @PathVariable String id) {
        Optional<Cliente> cliente = clienteService.getClienteById(id);
        return cliente.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
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