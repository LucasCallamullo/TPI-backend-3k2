package com.tpi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTOs comunes utilizados por múltiples controllers
 */
public class CommonDTOs {
    
    /**
     * DTO para datos de contenedor (Request)
     */
    public record ContenedorRequestDTO(
        BigDecimal peso,
        BigDecimal volumen, 
        String dimensiones,
        String tipoContenedor
    ) {}
    
    /**
     * DTO para datos de contenedor (Response)
     */
    public record ContenedorResponseDTO(
        String id,
        BigDecimal peso,
        BigDecimal volumen,
        String dimensiones, 
        String estado,
        String ubicacionActual,
        String solicitudId
    ) {}
    
    /**
     * DTO para datos de cliente (Request)
     */
    public record ClienteRequestDTO(
        String nombre,
        String apellido, 
        String email,
        String telefono,
        String documentoIdentidad
    ) {}
    
    /**
     * DTO para datos de cliente (Response)  
     */
    public record ClienteResponseDTO(
        String id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String documentoIdentidad,
        LocalDateTime fechaRegistro
    ) {}
    
    /**
     * DTO para coordenadas geográficas
     */
    public record CoordenadasDTO(
        Double latitud,
        Double longitud
    ) {}
}