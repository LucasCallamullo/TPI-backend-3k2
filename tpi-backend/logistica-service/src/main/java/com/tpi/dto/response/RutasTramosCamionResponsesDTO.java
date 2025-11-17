package com.tpi.dto.response;

import java.util.Date;
import java.util.List;

import com.tpi.model.Camion;
import com.tpi.model.Ruta;
import com.tpi.model.Tramo;
import com.tpi.dto.response.UbicacionDTOs.UbicacionResponseDTO;;

public class RutasTramosCamionResponsesDTO {
    
    public record RutaTramosCamionResponse(
        Long rutaId,
        Long solicitudId,
        Integer cantidadTramos,
        Integer cantidadDepositos,
        List<TramoConDetalles> tramos
    ) {
        public static RutaTramosCamionResponse of(Ruta ruta, List<TramoConDetalles> tramos) {
            return new RutaTramosCamionResponse(
                ruta.getId(),
                ruta.getSolicitudId(),
                ruta.getCantidadTramos(),
                ruta.getCantidadDepositos(),
                tramos
            );
        }
    }

    public record TramoConDetalles(
        Long id,
        Integer orden,
        UbicacionResponseDTO origen,
        UbicacionResponseDTO destino,
        String tipo,
        String estado,
        Double costoAproximado,
        Double costoReal,
        Integer diasEstadia,
        Date fechaInicio,
        Date fechaLlegada,
        Date fechaFin,
        CamionResponse camion
    ) {
        public static TramoConDetalles of(Tramo tramo) {
            return new TramoConDetalles(
                tramo.getId(),
                tramo.getOrden(),
                UbicacionResponseDTO.fromEntity(tramo.getOrigen()),  // ← Conversión aquí
                UbicacionResponseDTO.fromEntity(tramo.getDestino()), // ← Conversión aquí
                tramo.getTipo().getNombre(),
                tramo.getEstado().getNombre(),
                tramo.getCostoAproximado(),
                tramo.getCostoReal(),
                tramo.getDiasEstadia(),
                tramo.getFechaHoraInicio(),
                tramo.getFechaHoraLlegada(),
                tramo.getFechaHoraFin(),
                tramo.getCamion() != null ? 
                    CamionResponse.of(tramo.getCamion())
                    : null
            );
        }
    }

    public record CamionResponse(
        Long id,
        String dominio,
        String nombreConductor
    ) {
        public static CamionResponse of(Camion camion) {
            return new CamionResponse(
                camion.getId(),
                camion.getDominio(),
                camion.getNombreConductor()
            );
        }
    }
}