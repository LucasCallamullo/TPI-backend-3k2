package com.tpi.exception;

public class ContenedorNoDisponibleException extends RuntimeException {
    
    public ContenedorNoDisponibleException(String message) {
        super(message);
    }
    
    // Constructor opcional más específico
    public ContenedorNoDisponibleException(String identificacion, String estadoActual) {
        super(String.format(
            "El contenedor %s no está disponible. Estado actual: %s", 
            identificacion, estadoActual
        ));
    }
}