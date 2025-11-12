package com.tpi.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.tpi.dto.CommonDTOs.*;

/**
 * Clase contenedora para todos los DTOs relacionados con Solicitudes
 * Agrupa Request y Response DTOs para mejor organización
 */
public class SolicitudDTOs {
    
    // ==================== REQUEST DTOs ====================
    
    /**
     * DTO para crear una nueva solicitud de transporte
     * Incluye datos del contenedor y cliente
     */
    public record CrearSolicitudRequestDTO(
        ContenedorRequestDTO contenedor,
        ClienteRequestDTO cliente,
        String direccionOrigen,
        String direccionDestino,
        CoordenadasDTO coordenadasOrigen,
        CoordenadasDTO coordenadasDestino
    ) {}
    
    /**
     * DTO para asignar una ruta a una solicitud existente
     * Contiene la lista de tramos que componen la ruta
     */
    public record AsignarRutaRequestDTO(
        String rutaId,
        List<TramoRequestDTO> tramos
    ) {}
    
    /**
     * DTO para actualizar el estado de una solicitud
     * Usa estadoId en lugar del nombre para mayor consistencia
     */
    public record ActualizarEstadoRequestDTO(
        Long estadoId,           // ID del estado en lugar del nombre
        String observaciones     // Observaciones opcionales del cambio
    ) {}
    
    /**
     * DTO para finalizar una solicitud con costos y tiempos reales
     */
    public record FinalizarSolicitudRequestDTO(
        BigDecimal costoReal,    // Costo final calculado
        Duration tiempoReal,     // Tiempo real de entrega
        String observaciones     // Observaciones del cierre
    ) {}
    
    // ==================== RESPONSE DTOs ====================
    
    /**
     * DTO de respuesta con todos los datos de una solicitud
     */
    public record SolicitudResponseDTO(
        String id,                      // Identificador único de la solicitud
        String estado,                  // Estado actual (nombre del estado)
        ContenedorResponseDTO contenedor, // Datos del contenedor
        ClienteResponseDTO cliente,     // Datos del cliente
        BigDecimal costoEstimado,       // Costo estimado inicial
        BigDecimal costoFinal,          // Costo final real
        Duration tiempoEstimado,        // Tiempo estimado inicial  
        Duration tiempoReal,            // Tiempo real de entrega
        RutaResponseDTO ruta,           // Ruta asignada (si existe)
        LocalDateTime fechaCreacion,    // Fecha de creación
        LocalDateTime fechaActualizacion // Fecha última actualización
    ) {}
    
    /**
     * DTO especializado para consulta de estado y seguimiento
     */
    public record EstadoSolicitudResponseDTO(
        String solicitudId,             // ID de la solicitud
        String estado,                  // Estado actual
        String ubicacionActual,         // Ubicación actual del contenedor
        LocalDateTime ultimaActualizacion, // Fecha última actualización
        List<EventoSeguimientoDTO> historial // Historial de eventos
    ) {}
    
    // ==================== DTOs ANIDADOS ====================
    
    /**
     * DTO para datos de tramos en rutas
     */
    public record TramoRequestDTO(
        String origen,          // Ubicación origen del tramo
        String destino,         // Ubicación destino del tramo  
        String tipoTramo,       // Tipo: ORIGEN_DEPOSITO, DEPOSITO_DEPOSITO, etc.
        Integer orden           // Orden del tramo en la ruta
    ) {}
    
    /**
     * DTO de respuesta para tramos
     */
    public record TramoResponseDTO(
        String id,
        String origen,
        String destino, 
        String tipoTramo,
        String estado,
        BigDecimal costoAproximado,
        BigDecimal costoReal,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        String camionId,
        Integer orden
    ) {}
    
    /**
     * DTO para eventos de seguimiento
     */
    public record EventoSeguimientoDTO(
        LocalDateTime fechaHora,    // Fecha y hora del evento
        String evento,              // Descripción del evento
        String descripcion,         // Detalles adicionales
        String ubicacion            // Ubicación donde ocurrió
    ) {}
    
    /**
     * DTO de respuesta para rutas
     */
    public record RutaResponseDTO(
        String id,
        List<TramoResponseDTO> tramos,
        Integer cantidadTramos,
        Integer cantidadDepositos
    ) {}
}