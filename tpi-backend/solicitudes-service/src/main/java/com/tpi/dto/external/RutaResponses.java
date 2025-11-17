package com.tpi.dto.external;

import java.util.Date;
import java.util.List;
import com.tpi.dto.external.UbicacionResponses.UbicacionResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class RutaResponses {
    
    @Schema(description = "Respuesta con los detalles de la ruta asignada")
    public record RutaAsignadaResponseDTO(
        @Schema(description = "ID único de la ruta", example = "1")
        Long rutaId,
        
        @Schema(description = "Lista de tramos que componen la ruta")
        List<TramoResumenDTO> tramos,
        
        @Schema(description = "Cantidad total de tramos", example = "3")
        Integer cantidadTramos,
        
        @Schema(description = "Distancia total en kilómetros", example = "1200.5")
        Double distanciaTotal,
        
        @Schema(description = "Duración total estimada en horas", example = "14.2")
        Double duracionTotalHoras,
        
        @Schema(description = "Fecha y hora de creación de la ruta")
        Date fechaCreacion
    ) {}
    
    public record TramoResumenDTO(
        Long tramoId,
        Integer orden,
        UbicacionResponseDTO origen,
        UbicacionResponseDTO destino,
        Double distanciaKm,
        Double duracionHoras,
        String tipoTramo
    ) {}


    /**
     * Variante de DTO MUCCHO MAS DETALLADA
     */
    public record RutaTramosCamionResponse(
        Long rutaId,
        Long solicitudId,
        Integer cantidadTramos,
        Integer cantidadDepositos,
        List<TramoConDetalles> tramos
    ) {}
    
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
    ) {}
    
    public record CamionResponse(
        Long id,
        String dominio,
        String nombreConductor
    ) {}
}