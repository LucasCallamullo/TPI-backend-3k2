package com.tpi.controller;

import com.tpi.dto.SincronizarClienteRequest;
import com.tpi.model.Cliente;
import com.tpi.service.ClienteService;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    // Inyección de dependencias del servicio mediante constructor
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Endpoint para sincronizar cliente desde Keycloak
    @PostMapping("/sincronizar")
    public ResponseEntity<?> sincronizarCliente(@RequestBody SincronizarClienteRequest request) {
        try {
            System.out.println("Received sync request for: " + request.getKeycloakId());
            
            // Validaciones básicas de campos obligatorios
            if (request.getKeycloakId() == null || request.getKeycloakId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("keycloakId es requerido");
            }
            
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("nombre es requerido");
            }
            
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

    // Endpoint público sin requerir autenticación
    @GetMapping("/publico")
    public String publico() {
        return "Este endpoint es PUBLICO - Cualquiera puede verlo";
    }

    // Endpoint protegido que requiere rol CLIENTE
    @GetMapping("/hola-clientes")
    public String holaClientes() {
        return "Hola Cliente! Solo usuarios con rol CLIENTE pueden ver esto";
    }

    // Endpoint protegido que requiere rol ADMIN  
    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "Panel de Administracion - Solo ADMIN puede ver esto";
    }

    // Endpoint protegido que requiere cualquiera de los roles especificados
    @GetMapping("/operaciones")
    public String operaciones() {
        return "Operaciones - CLIENTE, OPERADOR o ADMIN pueden ver esto";
    }

    // Endpoint para obtener información del usuario autenticado
    @GetMapping("/mi-perfil")
    public String miPerfil(@AuthenticationPrincipal Jwt jwt) {
        // Extraer información del token JWT
        String username = jwt.getClaim("preferred_username");
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");
        
        return "Perfil de: " + username + 
               " | Roles: " + roles + 
               " | User ID: " + jwt.getSubject();
    }

    // DUPLICADO: Este endpoint está repetido (ver más abajo)
    @GetMapping("/{id}")
    public ResponseEntity<?> getCliente(@PathVariable String id) {
        Optional<Cliente> cliente = clienteService.getClienteById(id);
        return cliente.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para listar todos los clientes
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.getAllClientes();
    }
}