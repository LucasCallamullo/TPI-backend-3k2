package com.tpi.exception;

/**
 * Excepción personalizada que indica que una entidad no fue encontrada en el sistema.
 * Se usa cuando se busca un recurso por ID u otro identificador y no existe.
 */
public class EntidadNotFoundException extends RuntimeException {

    /**
     * Crea la excepción indicando el nombre de la entidad y el identificador buscado.
     *
     * @param entidad       Nombre de la entidad (Ej: "Camion", "Ruta").
     * @param identificador Valor del identificador que no fue encontrado.
     */
    public EntidadNotFoundException(String entidad, String identificador) {
        super(String.format("%s no encontrado: %s", entidad, identificador));
    }

    /**
     * Sobrecarga para cuando el identificador es un Long.
     */
    public EntidadNotFoundException(String entidad, Long id) {
        this(entidad, id.toString());
    }
}
