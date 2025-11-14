package com.tpi.exception;

public class MicroservicioNoDisponibleException extends RuntimeException {
    public MicroservicioNoDisponibleException(String microservicio, String operacion, Throwable cause) {
        super(String.format("Error comunicándose con %s durante %s", microservicio, operacion), cause);
    }
}