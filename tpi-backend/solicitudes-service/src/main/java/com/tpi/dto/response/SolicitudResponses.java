package com.tpi.dto.response;

import com.tpi.dto.external.RutaResponses.RutaAsignadaResponseDTO;
import com.tpi.dto.external.RutaResponses.RutaTramosCamionResponse;
import com.tpi.dto.external.UbicacionResponses.UbicacionResponseDTO;
import com.tpi.model.Solicitud;
import io.swagger.v3.oas.annotations.media.Schema;

public class SolicitudResponses {
    
    @Schema(description = "DTO básico para respuesta de solicitud (sin detalles de ruta)")
    public record SolicitudResponseDTO(
        @Schema(description = "ID único de la solicitud", example = "1")
        Long id,
        
        @Schema(
            description = "Estado actual de la solicitud",
            example = "PROGRAMADA"
        )
        String estado,
        
        @Schema(description = "Información del contenedor asociado")
        ContenedorResponseDTO contenedor,
        
        @Schema(description = "ID de la ubicación de origen", example = "1")
        Long origenId,
        
        @Schema(description = "ID de la ubicación de destino", example = "2")
        Long destinoId,
        
        @Schema(description = "Costo estimado del transporte", example = "45000.50")
        Double costoEstimado,
        
        @Schema(description = "Tiempo estimado de entrega", example = "48 horas")
        Double tiempoEstimado
    ) {
        public static SolicitudResponseDTO fromEntity(Solicitud solicitud) {
            return new SolicitudResponseDTO(
                solicitud.getId(),
                solicitud.getEstado().getNombre(),
                ContenedorResponseDTO.fromEntity(solicitud.getContenedor()),
                solicitud.getOrigenId(),
                solicitud.getDestinoId(),
                solicitud.getCostoEstimado(),
                solicitud.getTiempoEstimado()
            );
        }
    }
    
    @Schema(description = "DTO completo para solicitud con detalles de ruta asignada")
    public record SolicitudWithRutaResponseDTO(
        @Schema(description = "ID único de la solicitud", example = "1")
        Long id,
        
        @Schema(description = "ID del cliente propietario de la solicitud", example = "usuario-123")
        String clienteId,
        
        @Schema(
            description = "Estado actual de la solicitud", 
            example = "PROGRAMADA"
        )
        String estado,
        
        @Schema(description = "Información del contenedor asociado")
        ContenedorResponseDTO contenedor,
        
        @Schema(description = "ID de la ubicación de origen", example = "1")
        Long origenId,
        
        @Schema(description = "ID de la ubicación de destino", example = "2") 
        Long destinoId,
        
        @Schema(description = "Costo estimado del transporte", example = "45000.50")
        Double costoEstimado,
        
        @Schema(description = "Tiempo estimado de entrega", example = "48 horas")
        Double tiempoEstimado,
        
        @Schema(description = "Detalles completos de la ruta asignada")
        RutaAsignadaResponseDTO rutaAsignada
    ) {
        public static SolicitudWithRutaResponseDTO fromEntity(
            Solicitud solicitud, RutaAsignadaResponseDTO ruta) {
            
            return new SolicitudWithRutaResponseDTO(
                solicitud.getId(),
                solicitud.getClienteId(), 
                solicitud.getEstado().getNombre(), 
                ContenedorResponseDTO.fromEntity(solicitud.getContenedor()),
                solicitud.getOrigenId(),
                solicitud.getDestinoId(),
                solicitud.getCostoEstimado(), 
                solicitud.getTiempoEstimado(), 
                ruta
            );
        }
    }

    @Schema(description = "DTO completo para solicitud con detalles de ruta asignada")
    public record SolicitudWithUbicacionAndRutaResponseDTO(
        @Schema(description = "ID único de la solicitud", example = "1")
        Long id,
        
        @Schema(description = "ID del cliente propietario de la solicitud", example = "usuario-123")
        String clienteId,
        
        @Schema(
            description = "Estado actual de la solicitud", 
            example = "PROGRAMADA"
        )
        String estado,
        
        @Schema(description = "Información del contenedor asociado")
        ContenedorResponseDTO contenedor,
        
        UbicacionResponseDTO origenId,
        
        UbicacionResponseDTO destinoId,
        
        @Schema(description = "Costo estimado del transporte", example = "45000.50")
        Double costoEstimado,
        
        @Schema(description = "Tiempo estimado de entrega", example = "48 horas")
        Double tiempoEstimado,
        
        @Schema(description = "Detalles completos de la ruta asignada")
        RutaTramosCamionResponse rutaAsignada
    ) {
        public static SolicitudWithUbicacionAndRutaResponseDTO fromEntity(
            Solicitud solicitud, 
            UbicacionResponseDTO origen, 
            UbicacionResponseDTO destino, 
            RutaTramosCamionResponse ruta) {
            
            return new SolicitudWithUbicacionAndRutaResponseDTO(
                solicitud.getId(),
                solicitud.getClienteId(), 
                solicitud.getEstado().getNombre(), 
                ContenedorResponseDTO.fromEntity(solicitud.getContenedor()),
                origen,
                destino,
                solicitud.getCostoEstimado(), 
                solicitud.getTiempoEstimado(), 
                ruta
            );
        }
    }


    
    @Schema(description = "DTO para respuesta de actualización de estado de solicitud")
    public record SolicitudUpdateEstadoResponseDTO(
        @Schema(description = "ID único de la solicitud", example = "1")
        Long id,
        
        @Schema(
            description = "Nuevo estado de la solicitud",
            example = "PROGRAMADA"
        )
        String estado,
        
        @Schema(
            description = "Mensaje descriptivo de la operación",
            example = "Solicitud programada exitosamente"
        )
        String mensaje
    ) {
        public static SolicitudUpdateEstadoResponseDTO fromEntity(Solicitud solicitud) {
            return new SolicitudUpdateEstadoResponseDTO(
                solicitud.getId(),
                solicitud.getEstado().getNombre(),
                "Solicitud " + solicitud.getEstado().getNombre() + " exitosamente"
            );
        }
    }
}