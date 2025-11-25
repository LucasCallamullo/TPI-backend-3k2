package com.tpi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para la API REST.
 * Intercepta excepciones lanzadas por los controladores y devuelve
 * respuestas HTTP estandarizadas con un body de tipo ErrorResponse.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja casos donde no se encuentra la entidad solicitada.
     * Devuelve 404 NOT FOUND.
     */
    @ExceptionHandler(EntidadNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntidadNotFound(EntidadNotFoundException ex) {
        log.warn("Entidad no encontrada: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "ENTIDAD_NO_ENCONTRADA",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja accesos denegados.
     * Devuelve 403 FORBIDDEN.
     */
    @ExceptionHandler(AccessoDenegadoException.class)
    public ResponseEntity<ErrorResponse> handleAccesoDenegado(AccessoDenegadoException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "ACCESO_DENEGADO", 
            ex.getMessage(),
            HttpStatus.FORBIDDEN.value()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Maneja excepciones genéricas no controladas.
     * Devuelve 500 INTERNAL SERVER ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Error interno del servidor: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
            "ERROR_INTERNO",
            "Ha ocurrido un error interno en el servidor",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Maneja errores de comunicación con microservicios.
     * Devuelve 503 SERVICE UNAVAILABLE.
     */
    @ExceptionHandler(MicroservicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleMicroservicioNoDisponible(MicroservicioNoDisponibleException ex) {
        log.error("Error de comunicación: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse("MICROSERVICIO_NO_DISPONIBLE", ex.getMessage(), 503));
    }

    /**
     * Maneja casos donde se intenta crear una entidad que ya existe.
     * Devuelve 409 CONFLICT.
     */
    @ExceptionHandler(EntidadDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleEntidadDuplicada(EntidadDuplicadaException ex) {
        log.warn("Entidad duplicada: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "ENTIDAD_DUPLICADA",
            ex.getMessage(),
            HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
