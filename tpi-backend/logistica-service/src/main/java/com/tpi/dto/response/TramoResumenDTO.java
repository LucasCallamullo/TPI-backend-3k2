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
    Double duracionHoras,
    TipoTramoDTO tipo,
    EstadoTramoDTO estado
    // String tipoTramo
) {
    public static TramoResumenDTO fromEntity(Tramo tramo) {

        Double horas = round2(tramo.getDuracionEstimadaSegundos());
        TipoTramoDTO tipoDto = TipoTramoDTO.fromEntity(tramo.getTipo());
        EstadoTramoDTO estadoDto = EstadoTramoDTO.fromEntity(tramo.getEstado());

        return new TramoResumenDTO(
            tramo.getId(),
            tramo.getOrden(),
            tramo.getOrigen() != null ? 
                UbicacionResponseDTO.fromEntity(tramo.getOrigen()) : null,
            tramo.getDestino() != null ? 
                UbicacionResponseDTO.fromEntity(tramo.getDestino()) : null,
            tramo.getDistanciaKm(),
            horas,
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

    /**
     * Redondea un valor Double a dos decimales usando Math.round().
     */
    private static Double round2(Long value) {
        if (value == null) return 0.0;

        return Math.round(value * 100.0) / 100.0;
    }
}