package com.tpi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tpi.model.Solicitud;

public record SolicitudResponseDTO(
    Long id,
    String estado,
    ContenedorResponseDTO contenedor,
    Long origenId,
    Long destinoId,
    BigDecimal costoEstimado,
    Integer tiempoEstimado,
    BigDecimal costoFinal,
    Integer tiempoReal,
    LocalDateTime fechaCreacion
) {

    // Constructor que extrae los IDs automáticamente desde la entidad
    public static SolicitudResponseDTO fromEntity(Solicitud solicitud) {
        return new SolicitudResponseDTO(
            solicitud.getId(),
            solicitud.getEstado().getNombre(),
            ContenedorResponseDTO.fromEntity(solicitud.getContenedor()),
            solicitud.getOrigenId(),
            solicitud.getDestinoId(),
            solicitud.getCostoEstimado(),
            solicitud.getTiempoEstimado(),
            solicitud.getCostoFinal(),
            solicitud.getTiempoReal(),
            solicitud.getFechaCreacion()
        );
    }

    // Constructor estático en el DTO con parametros externos
    public static SolicitudResponseDTO fromEntity(Solicitud solicitud, Long origenId, Long destinoId) {
        return new SolicitudResponseDTO(
            solicitud.getId(),
            solicitud.getEstado().getNombre(),
            ContenedorResponseDTO.fromEntity(solicitud.getContenedor()),
            origenId,
            destinoId,
            solicitud.getCostoEstimado(),
            solicitud.getTiempoEstimado(),
            solicitud.getCostoFinal(),
            solicitud.getTiempoReal(),
            solicitud.getFechaCreacion()
        );
    }
}