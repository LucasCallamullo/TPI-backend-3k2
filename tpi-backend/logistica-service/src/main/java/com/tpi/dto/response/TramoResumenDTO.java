package com.tpi.dto.response;

import com.tpi.dto.response.UbicacionDTOs.UbicacionResponseDTO;
import com.tpi.model.Tramo;

public record TramoResumenDTO(
    Long tramoId,
    Integer orden,
    UbicacionResponseDTO origen,
    UbicacionResponseDTO destino,
    Double distanciaKm,
    Double duracionHoras,
    String tipoTramo
) {
    public static TramoResumenDTO fromEntity(Tramo tramo) {
        return new TramoResumenDTO(
            tramo.getId(),
            tramo.getOrden(),
            tramo.getOrigen() != null ? 
                UbicacionResponseDTO.fromEntity(tramo.getOrigen()) : null,
            tramo.getDestino() != null ? 
                UbicacionResponseDTO.fromEntity(tramo.getDestino()) : null,
            tramo.getDistanciaKm(),
            tramo.getDuracionEstimadaSegundos() != null ? 
                tramo.getDuracionEstimadaSegundos() / 3600.0 : null,
                
            tramo.getTipo() != null ? tramo.getTipo().getNombre() : "NO_ASIGNADO"
        );
    }
}