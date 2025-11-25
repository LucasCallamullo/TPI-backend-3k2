package com.tpi.exception;

import java.time.LocalDateTime;

/**
 * Representa un error estándar devuelto por la API.
 * Incluye código interno, mensaje, estado HTTP, timestamp y el path donde ocurrió.
 */
public record ErrorResponse(
    String codigo,      // Código interno del error (ej: "NOT_FOUND", "DUPLICADO")
    String mensaje,     // Mensaje descriptivo para el usuario o el cliente
    Integer status,     // Código HTTP asociado al error
    LocalDateTime timestamp, // Momento en el que ocurrió el error
    String path         // (Opcional) Endpoint donde ocurrió el error
) {

    /**
     * Constructor simplificado que inicializa automáticamente el timestamp
     * y deja el path en null cuando no se necesita.
     */
    public ErrorResponse(String codigo, String mensaje, Integer status) {
        this(codigo, mensaje, status, LocalDateTime.now(), null);
    }
}
