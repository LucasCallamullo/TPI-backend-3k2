package com.tpi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(MicroservicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleMicroservicioNoDisponible(MicroservicioNoDisponibleException ex) {
        log.error("Error de comunicación: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse("MICROSERVICIO_NO_DISPONIBLE", ex.getMessage(), 503));
    }

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