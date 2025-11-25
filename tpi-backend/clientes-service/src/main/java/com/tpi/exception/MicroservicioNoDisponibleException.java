package com.tpi.exception;

/**
 * Excepción personalizada que indica que un microservicio no está disponible
 * o hubo un error al comunicarse con él.
 */
public class MicroservicioNoDisponibleException extends RuntimeException {

    /**
     * Crea una nueva excepción indicando el microservicio y la operación
     * que falló, con la causa original del error.
     *
     * @param microservicio Nombre del microservicio que no respondió.
     * @param operacion Operación o acción que se intentaba realizar.
     * @param cause Excepción original que causó el fallo.
     */
    public MicroservicioNoDisponibleException(String microservicio, String operacion, Throwable cause) {
        super(String.format("Error comunicándose con %s durante %s", microservicio, operacion), cause);
    }
}
