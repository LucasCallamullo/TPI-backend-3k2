package com.tpi.dto.response;

import com.tpi.dto.external.RutaResponses.RutaAsignadaResponseDTO;
import com.tpi.dto.external.RutaResponses.RutaTramosCamionResponse;
import com.tpi.dto.external.UbicacionResponses.UbicacionResponseDTO;
import com.tpi.model.EstadoSolicitud;
import com.tpi.model.Solicitud;
import io.swagger.v3.oas.annotations.media.Schema;

public class SolicitudResponses {

    /**
     * DTO simple que representa el estado de una solicitud.
     */
    public record EstadoSolicitudDTO(
        @Schema(description = "ID del estado", example = "2")
        Long id,

        @Schema(description = "Nombre del estado", example = "PROGRAMADA")
        String nombre
    ) {
        public static EstadoSolicitudDTO fromEntity(EstadoSolicitud estado) {
            return new EstadoSolicitudDTO(
                estado.getId(),
                estado.getNombre()
            );
        }
    }

    /**
     * DTO que representa información de un cliente.
     */
    public record ClienteDTO(
        @Schema(description = "ID del cliente (keycloakId)", example = "123e4567-e89b-12d3-a456-426614174000")
        String id,

        @Schema(description = "Nombre completo del cliente", example = "Juan Pérez")
        String nombre,

        @Schema(description = "Email del cliente", example = "juan.perez@email.com")
        String email,

        @Schema(description = "Teléfono de contacto", example = "+54 9 11 1234-5678")
        String telefono,

        @Schema(description = "Dirección principal del cliente", example = "Av. Corrientes 1234, CABA")
        String direccion
    ) {}


    @Schema(description = "DTO básico para respuesta de solicitud (sin detalles de ruta, ni ubicaciones)")
    public record SolicitudResponseDTO(
        @Schema(description = "ID único de la solicitud", example = "1")
        Long id,
        
        @Schema(description = "Estado actual de la solicitud representado como objeto")
        EstadoSolicitudDTO estado,

        @Schema(description = "Cliente de la solicitud representado como objeto")
        ClienteDTO cliente,

        @Schema(description = "Información del contenedor asociado")
        ContenedorResponseDTO contenedor,
        
        @Schema(description = "ID de la ubicación de origen", example = "1")
        Long origen,
        
        @Schema(description = "ID de la ubicación de destino", example = "2")
        Long destino,
        
        @Schema(description = "Distancia total en km destinados", example = "1500.50")
        Double distanciaTotalKM,

        @Schema(description = "Costo estimado del transporte", example = "45000.50")
        Double costoEstimado,
        
        @Schema(description = "Tiempo estimado de entrega", example = "48 horas")
        Double tiempoEstimado
    ) {
        // Metodo para construir utilizando el estado previamente pedido
        public static SolicitudResponseDTO fromEntity(
            Solicitud solicitud, EstadoSolicitud estado, ClienteDTO cliente) {

            var estadoSolicitud = EstadoSolicitudDTO.fromEntity(estado);
            var contenedorDTO = ContenedorResponseDTO.fromEntity(solicitud.getContenedor());

            return new SolicitudResponseDTO(
                solicitud.getId(),
                estadoSolicitud,
                cliente,
                contenedorDTO,
                solicitud.getOrigenId(),
                solicitud.getDestinoId(),
                solicitud.getDistanciaTotalKM(),
                solicitud.getCostoEstimado(),
                solicitud.getTiempoEstimadoHoras()
            );
        }
    }
    
    @Schema(description = 
            """
            DTO básico para respuesta de solicitud y sus ubicaciones (sin detalles de ruta)
        """
    )
    public record SolicitudWithUbicacionResponseDTO(
        @Schema(description = "ID único de la solicitud", example = "1")
        Long id,
        
        @Schema(description = "Estado actual de la solicitud representado como objeto")
        EstadoSolicitudDTO estado,

        @Schema(description = "Cliente de la solicitud representado como objeto")
        ClienteDTO cliente,
        
        @Schema(description = "Información del contenedor asociado")
        ContenedorResponseDTO contenedor,
        
        @Schema(description = "ID de la ubicación de origen", example = "1")
        UbicacionResponseDTO origen,
        
        @Schema(description = "ID de la ubicación de destino", example = "2")
        UbicacionResponseDTO destino,
        
        @Schema(description = "Costo estimado del transporte", example = "45000.50")
        Double costoEstimado,
        
        @Schema(description = "Tiempo estimado de entrega", example = "48 horas")
        Double tiempoEstimado
    ) {
        public static SolicitudWithUbicacionResponseDTO fromEntity(
            Solicitud solicitud, EstadoSolicitud estado,
            UbicacionResponseDTO origen, UbicacionResponseDTO destino, ClienteDTO cliente) {

            // Obtener dto auxiliares
            var estadoDTO = EstadoSolicitudDTO.fromEntity(estado);
            var contenedorDTO = ContenedorResponseDTO.fromEntity(solicitud.getContenedor());

            return new SolicitudWithUbicacionResponseDTO(
                solicitud.getId(),
                estadoDTO,
                cliente,
                contenedorDTO,
                origen,
                destino,
                solicitud.getCostoEstimado(),
                solicitud.getTiempoEstimadoHoras()
            );
        }
    }


    @Schema(description = "DTO completo para solicitud con detalles de ruta asignada")
    public record SolicitudWithRutaResponseDTO(
        @Schema(description = "ID único de la solicitud", example = "1")
        Long id,
        
        @Schema(description = "Estado actual de la solicitud representado como objeto")
        EstadoSolicitudDTO estado,

        @Schema(description = "Cliente de la solicitud representado como objeto")
        ClienteDTO cliente,
        
        @Schema(description = "Información del contenedor asociado")
        ContenedorResponseDTO contenedor,
        
        @Schema(description = "ID de la ubicación de origen", example = "1")
        Long origenId,
        
        @Schema(description = "ID de la ubicación de destino", example = "2") 
        Long destinoId,
        
        @Schema(description = "Distancia total en km destinados", example = "1500.50")
        Double distanciaTotalKM,

        @Schema(description = "Costo estimado del transporte", example = "45000.50")
        Double costoEstimado,
        
        @Schema(description = "Tiempo estimado en horas de entrega", example = "48 horas")
        Double tiempoEstimadoHoras,
        
        @Schema(description = "Detalles completos de la ruta asignada")
        RutaAsignadaResponseDTO ruta
    ) {
        public static SolicitudWithRutaResponseDTO fromEntity(
            Solicitud solicitud, RutaAsignadaResponseDTO ruta, ClienteDTO cliente) {

            // Obtener DTO auxiliares
            var estadoDTO = EstadoSolicitudDTO.fromEntity(solicitud.getEstado());
            var contenedorDTO = ContenedorResponseDTO.fromEntity(solicitud.getContenedor());
            
            return new SolicitudWithRutaResponseDTO(
                solicitud.getId(),
                estadoDTO, 
                cliente,
                contenedorDTO,
                solicitud.getOrigenId(),
                solicitud.getDestinoId(),
                solicitud.getDistanciaTotalKM(), 
                solicitud.getCostoEstimado(), 
                solicitud.getTiempoEstimadoHoras(), 
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
            description = "Estado actual de la solicitud representado como objeto",
            example = """
                {
                    "id": 2,
                    "nombre": "PROGRAMADA"
                }
            """
        )
        EstadoSolicitudDTO estado,
        
        @Schema(description = "Información del contenedor asociado")
        ContenedorResponseDTO contenedor,
        
        UbicacionResponseDTO origenId,
        
        UbicacionResponseDTO destinoId,
        
        @Schema(description = "Distancia total en km destinados", example = "1500.50")
        Double distanciaTotalKM,

        @Schema(description = "Costo estimado del transporte", example = "45000.50")
        Double costoEstimado,
        
        @Schema(description = "Tiempo estimado de entrega", example = "48 horas")
        Double tiempoEstimado,
        
        @Schema(description = "Detalles completos de la ruta asignada")
        RutaTramosCamionResponse ruta
    ) {
        public static SolicitudWithUbicacionAndRutaResponseDTO fromEntity(
            Solicitud solicitud, UbicacionResponseDTO origen, 
            UbicacionResponseDTO destino, RutaTramosCamionResponse ruta) {
            
            var estadoDTO = EstadoSolicitudDTO.fromEntity(solicitud.getEstado());
            var contenedorDTO = ContenedorResponseDTO.fromEntity(solicitud.getContenedor());
            

            return new SolicitudWithUbicacionAndRutaResponseDTO(
                solicitud.getId(),
                solicitud.getClienteId(), 
                estadoDTO, 
                contenedorDTO,
                origen,
                destino,
                solicitud.getDistanciaTotalKM(), 
                solicitud.getCostoEstimado(), 
                solicitud.getTiempoEstimadoHoras(), 
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