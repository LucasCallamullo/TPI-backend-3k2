package com.tpi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clientes")
public class DebugController {
    

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
    @GetMapping("/hola-admin")
    public String adminDashboard() {
        return "Panel de Administracion - Solo ADMIN puede ver esto";
    }

    // Endpoint protegido que requiere cualquiera de los roles especificados
    @GetMapping("/multi-rol")
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
}