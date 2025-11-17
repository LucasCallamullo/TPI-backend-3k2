package com.tpi.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    String codigo,      // Código de error identificable
    String mensaje,     // Mensaje para el usuario
    Integer status,     // Código HTTP
    LocalDateTime timestamp,
    String path         // Opcional: endpoint donde ocurrió
) {
    public ErrorResponse(String codigo, String mensaje, Integer status) {
        this(codigo, mensaje, status, LocalDateTime.now(), null);
    }
}