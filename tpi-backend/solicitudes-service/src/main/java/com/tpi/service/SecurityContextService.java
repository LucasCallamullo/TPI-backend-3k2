package com.tpi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {
    
    /**
     * Obtiene el token JWT completo desde el contexto de seguridad de Spring
     * Primero intenta obtenerlo desde credentials y luego desde el principal
     */
    public String obtenerJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        throw new SecurityException("No se pudo obtener el JWT del contexto de seguridad");
    }
    
    /**
     * Obtiene el ID del cliente desde el token JWT de autenticación
     * El clienteId se extrae del subject del token JWT
     */
    public String obtenerClienteIdDesdeToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getSubject();
        }
        throw new SecurityException("No se pudo obtener el cliente ID del token");
    }
}