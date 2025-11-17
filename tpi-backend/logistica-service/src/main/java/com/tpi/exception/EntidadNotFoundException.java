package com.tpi.exception;

public class EntidadNotFoundException extends RuntimeException {
    public EntidadNotFoundException(String entidad, String identificador) {
        super(String.format("%s no encontrado: %s", entidad, identificador));
    }
    
    public EntidadNotFoundException(String entidad, Long id) {
        this(entidad, id.toString());
    }
}