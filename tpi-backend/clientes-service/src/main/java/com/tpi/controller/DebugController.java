package com.tpi.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Debug", description = "Endpoints de prueba para seguridad y roles")
public class DebugController {
    
    @Operation(
        summary = "Endpoint público",
        description = "Endpoint sin requerir autenticación - accesible para cualquier usuario"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje público retornado exitosamente")
    })
    @GetMapping("/publico")
    public String publico() {
        return "Este endpoint es PUBLICO - Cualquiera puede verlo";
    }

    @Operation(
        summary = "Saludo para clientes", 
        description = "Endpoint protegido que requiere rol CLIENTE",
        security = @SecurityRequirement(name = "OAuth2")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saludo para cliente retornado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol CLIENTE")
    })
    @GetMapping("/hola-clientes")
    public String holaClientes() {
        return "Hola Cliente! Solo usuarios con rol CLIENTE pueden ver esto";
    }

    @Operation(
        summary = "Panel de administración",
        description = "Endpoint protegido que requiere rol ADMIN",
        security = @SecurityRequirement(name = "OAuth2")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Panel de admin retornado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol ADMIN")
    })
    @GetMapping("/hola-admin")
    public String adminDashboard() {
        return "Panel de Administracion - Solo ADMIN puede ver esto";
    }

    @Operation(
        summary = "Endpoint multi-rol",
        description = "Endpoint protegido que requiere cualquiera de los roles: CLIENTE, OPERADOR o ADMIN",
        security = @SecurityRequirement(name = "OAuth2")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operaciones retornadas"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene ninguno de los roles requeridos")
    })
    @GetMapping("/multi-rol")
    public String operaciones() {
        return "Operaciones - CLIENTE, OPERADOR o ADMIN pueden ver esto";
    }

    @Operation(
        summary = "Perfil del usuario",
        description = "Endpoint para obtener información del usuario autenticado a partir del token JWT",
        security = @SecurityRequirement(name = "OAuth2")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Información del perfil retornada"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/mi-perfil")
    public String miPerfil(@AuthenticationPrincipal Jwt jwt) {
        // Extraer información del token JWT
        String username = jwt.getClaim("preferred_username");
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");
        
        return "Perfil de: " + username + 
               " | Roles: " + roles + 
               " | User ID: " + jwt.getSubject();
    }
}