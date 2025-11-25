package com.tpi.exception;

/**
 * Excepción personalizada para indicar que un usuario intentó acceder
 * a un recurso o acción sin los permisos necesarios.
 */
public class AccessoDenegadoException extends RuntimeException {

    /**
     * Crea una nueva excepción de acceso denegado con un mensaje descriptivo.
     *
     * @param mensaje Mensaje que explica por qué se denegó el acceso.
     */
    public AccessoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
