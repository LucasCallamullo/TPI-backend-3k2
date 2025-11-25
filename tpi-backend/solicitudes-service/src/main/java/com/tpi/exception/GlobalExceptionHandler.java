package com.tpi.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    
    @ExceptionHandler(ContenedorNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleContenedorNoDisponible(
        ContenedorNoDisponibleException ex,
        HttpServletRequest request) {  // ← Agregar este parámetro
        log.warn("Contenedor no disponible: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "CONTENEDOR_NO_DISPONIBLE",  // Código de error específico
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),  // 409 Conflict es apropiado
            request.getRequestURI()  // ← Esto te da el path
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    /*
    *     ERRORES GENERICOS COPIAR Y PEGAR EN CADA MS
    */
    @ExceptionHandler(EntidadNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntidadNotFound(
        EntidadNotFoundException ex, HttpServletRequest request) {
            
        log.warn("Entidad no encontrada: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "ENTIDAD_NO_ENCONTRADA",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI()  // ← Esto te da el path
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AccessoDenegadoException.class)
    public ResponseEntity<ErrorResponse> handleAccesoDenegado(
        AccessoDenegadoException ex, 
        HttpServletRequest request) {

        log.warn("Acceso denegado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "ACCESO_DENEGADO", 
            ex.getMessage(),
            HttpStatus.FORBIDDEN.value(),
            request.getRequestURI()  // ← Esto te da el path
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex, 
        HttpServletRequest request) {

        log.error("Error interno del servidor: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
            "ERROR_INTERNO",
            "Ha ocurrido un error interno en el servidor",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI()  // ← Esto te da el path
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MicroservicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleMicroservicioNoDisponible(
            MicroservicioNoDisponibleException ex,
            HttpServletRequest request) {  // ← Agregar HttpServletRequest si quieres el path
        
        log.error("Error de comunicación: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse(
                "MICROSERVICIO_NO_DISPONIBLE", 
                ex.getMessage(), 
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                request.getRequestURI()  // ← Agregar path si usas el constructor nuevo
            ));
    }

    @ExceptionHandler(EntidadDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleEntidadDuplicada(
        EntidadDuplicadaException ex,
        HttpServletRequest request) {
        log.warn("Entidad duplicada: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "ENTIDAD_DUPLICADA",
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),
            request.getRequestURI()  // ← Esto te da el path
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    


    
    // Manejar errores de JSON mal formado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {  // ← Agregar este parámetro
        
        log.warn("JSON mal formado recibido: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "JSON_INVALIDO",
            "El JSON enviado está mal formado o contiene tipos de datos incorrectos",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()  // ← Esto te da el path
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Manejar errores de validación @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {  // ← Agregar este parámetro
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse(
            "DATOS_INVALIDOS",
            "Error de validación en los datos de entrada: " + String.join(", ", errors),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()  // ← Esto te da el path
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Manejar errores de constraint violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse(
            "RESTRICCION_VIOLADA",
            "Violación de restricciones: " + String.join(", ", errors),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()  // ← Esto te da el path
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}