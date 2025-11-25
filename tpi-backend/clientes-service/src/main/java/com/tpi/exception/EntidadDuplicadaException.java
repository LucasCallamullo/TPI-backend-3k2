package com.tpi.exception;

/**
 * Excepción personalizada para indicar que una entidad ya existe en el sistema.
 * Se utiliza cuando se intenta crear o registrar un recurso con un campo que debe ser único.
 */
public class EntidadDuplicadaException extends RuntimeException {

    /**
     * Crea la excepción indicando el nombre de la entidad, el campo duplicado y su valor.
     *
     * @param entidad Nombre de la entidad (por ejemplo, "Camion", "Ruta").
     * @param campo   Nombre del campo que está duplicado (por ejemplo, "patente", "nombre").
     * @param valor   Valor que genera la duplicación.
     */
    public EntidadDuplicadaException(String entidad, String campo, String valor) {
        super(String.format("%s con %s '%s' ya existe", entidad, campo, valor));
    }

    /**
     * Sobrecarga que permite pasar el valor duplicado como Long.
     */
    public EntidadDuplicadaException(String entidad, String campo, Long valor) {
        this(entidad, campo, valor.toString());
    }
}
