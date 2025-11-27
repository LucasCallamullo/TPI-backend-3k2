package com.tpi.dto.response;

import com.tpi.dto.response.UbicacionDTOs.UbicacionResponseDTO;
import com.tpi.model.EstadoTramo;
import com.tpi.model.TipoTramo;
import com.tpi.model.Tramo;

public record TramoResumenDTO(
    Long id,
    Integer orden,
    UbicacionResponseDTO origen,
    UbicacionResponseDTO destino,
    Double distanciaKm,
    Long duracionSegundos,
    Double duracionHoras,
    TipoTramoDTO tipo,
    EstadoTramoDTO estado
) {
    /**
     * Convierte una entidad Tramo a un DTO de resumen (TramoResumenDTO).
     * Este DTO resume información esencial del tramo: ubicaciones, distancia,
     * duración estimada, horas calculadas, tipo y estado.
     *
     * @param tramo Entidad Tramo a transformar.
     * @return Instancia de TramoResumenDTO con los datos mapeados.
     */
    public static TramoResumenDTO fromEntity(Tramo tramo) {

        // Convertir segundos estimados de duración a horas (double).
        // Si es null, se asume 0.
        Double horas = tramo.getDuracionEstimadaSegundos() != null ? 
            tramo.getDuracionEstimadaSegundos() / 3600.0 : 0.0;

        // Convertir el tipo de tramo a su DTO correspondiente.
        TipoTramoDTO tipoDto = TipoTramoDTO.fromEntity(tramo.getTipo());

        // Convertir el estado del tramo a su DTO correspondiente.
        EstadoTramoDTO estadoDto = EstadoTramoDTO.fromEntity(tramo.getEstado());

        return new TramoResumenDTO(
            tramo.getId(),
            tramo.getOrden(),

            // Convertir ubicación origen (si existe)
            tramo.getOrigen() != null ? 
                UbicacionResponseDTO.fromEntity(tramo.getOrigen()) : null,

            // Convertir ubicación destino (si existe)
            tramo.getDestino() != null ? 
                UbicacionResponseDTO.fromEntity(tramo.getDestino()) : null,

            tramo.getDistanciaKm(),
            tramo.getDuracionEstimadaSegundos(),

            // Redondear las horas estimadas a 2 decimales
            Math.round(horas * 100.0) / 100.0,

            tipoDto,
            estadoDto
        );
    }

    
    public record TipoTramoDTO(
        Long id,
        String nombre
    ) {
        public static TipoTramoDTO fromEntity(TipoTramo tipo) {
            return new TipoTramoDTO(tipo.getId(), tipo.getNombre());
        }
    }

    
    public record EstadoTramoDTO(
        Long id,
        String nombre
    ) {
        public static EstadoTramoDTO fromEntity(EstadoTramo tipo) {
            return new EstadoTramoDTO(tipo.getId(), tipo.getNombre());
        }
    }
}