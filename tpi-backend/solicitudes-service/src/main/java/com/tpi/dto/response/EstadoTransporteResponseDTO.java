package com.tpi.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import com.tpi.model.Solicitud;

public record EstadoTransporteResponseDTO(
    Long id,
    String estado,
    String etapaActual,
    String ubicacionActual,
    Integer progreso,
    LocalDateTime fechaEstimadaEntrega,
    ContenedorInfoDTO contenedor
) {
    
    public static EstadoTransporteResponseDTO fromEntity(Solicitud solicitud) {
        Objects.requireNonNull(solicitud, "La solicitud no puede ser nula");
        
        return new EstadoTransporteResponseDTO(
            solicitud.getId(),
            solicitud.getEstado().getNombre(),
            determinarEtapaActual(solicitud),
            determinarUbicacionActual(solicitud),
            calcularProgreso(solicitud),
            calcularFechaEstimadaEntrega(solicitud),
            ContenedorInfoDTO.fromEntity(solicitud.getContenedor())
        );
    }
    
    private static String determinarEtapaActual(Solicitud solicitud) {
        return switch (solicitud.getEstado().getNombre()) {
            case "BORRADOR" -> "En preparación";
            case "PROGRAMADA" -> "Programado para envío";
            case "EN_TRANSITO" -> "En camino a destino";
            case "ENTREGADA" -> "Entregado";
            default -> "Estado desconocido: " + solicitud.getEstado().getNombre();
        };
    }
    
    private static String determinarUbicacionActual(Solicitud solicitud) {
        // Lógica para determinar ubicación basada en estado y rutas
        return switch (solicitud.getEstado().getNombre()) {
            case "BORRADOR" -> "En origen";
            case "PROGRAMADA" -> "En depósito de origen";
            case "EN_TRANSITO" -> "En tránsito";
            case "ENTREGADA" -> "Entregado en destino";
            default -> "Ubicación no disponible";
        };
    }
    
    private static Integer calcularProgreso(Solicitud solicitud) {
        // Lógica simple de progreso
        return switch (solicitud.getEstado().getNombre()) {
            case "BORRADOR" -> 0;
            case "PROGRAMADA" -> 25;
            case "EN_TRANSITO" -> 50;
            case "ENTREGADA" -> 100;
            default -> 0;
        };
    }
    
    private static LocalDateTime calcularFechaEstimadaEntrega(Solicitud solicitud) {
        // Lógica para calcular fecha estimada
        // Por ahora, simple estimación
        return solicitud.getFechaCreacion().plusDays(2);
    }

    
}

