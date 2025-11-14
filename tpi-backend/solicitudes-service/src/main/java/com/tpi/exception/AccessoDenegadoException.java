package com.tpi.exception;

public class AccessoDenegadoException extends RuntimeException {
    public AccessoDenegadoException(String mensaje) {
        super(mensaje);
    }
}