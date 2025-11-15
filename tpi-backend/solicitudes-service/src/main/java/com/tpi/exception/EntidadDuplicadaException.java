package com.tpi.exception;

public class EntidadDuplicadaException extends RuntimeException {
    public EntidadDuplicadaException(String entidad, String campo, String valor) {
        super(String.format("%s con %s '%s' ya existe", entidad, campo, valor));
    }
    
    public EntidadDuplicadaException(String entidad, String campo, Long valor) {
        this(entidad, campo, valor.toString());
    }
}