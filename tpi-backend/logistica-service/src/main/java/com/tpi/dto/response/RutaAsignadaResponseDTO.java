package com.tpi.dto.response;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.tpi.model.Ruta;
import com.tpi.model.Tarifa;
import com.tpi.model.Tramo;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Respuesta con los detalles de la ruta asignada")
public record RutaAsignadaResponseDTO(
    @Schema(description = "ID único de la ruta", example = "1")
    Long id,
    
    @Schema(description = "Lista de tramos que componen la ruta")
    List<TramoResumenDTO> tramos,
    
    @Schema(description = "Cantidad total de tramos", example = "3")
    Integer cantidadTramos,
    
    @Schema(description = "Distancia total en kilómetros", example = "1200.5")
    Double distanciaTotalKM,
    
    @Schema(description = "Duración total estimada en horas", example = "14.2")
    Double duracionTotalHoras,
    
    @Schema(description = "Fecha y hora de creación de la ruta")
    Date fechaCreacion
) {
    public static RutaAsignadaResponseDTO fromEntity(Ruta ruta, Tarifa tarifa, List<Tramo> tramos) {
        
        // Calcular totales
        Double distanciaTotalKM = calcularDistanciaTotal(tramos);
        Double duracionTotalHoras = calcularDuracionTotalHoras(tramos);
        
        // Mapear tramos a DTOs
        List<TramoResumenDTO> tramosDTO = tramos.stream()
            .map(TramoResumenDTO::fromEntity)
            .collect(Collectors.toList());
        
        return new RutaAsignadaResponseDTO(
            ruta.getId(),
            tramosDTO,
            tramos.size(),
            distanciaTotalKM,
            duracionTotalHoras,
            ruta.getFechaHoraCreacion() 
        );
    }
    
    /**
     * Calcula la distancia total en kilómetros sumando la distancia
     * de todos los tramos. Si un tramo no tiene distancia, se usa 0.
     * El resultado se redondea a dos decimales.
     */
    private static Double calcularDistanciaTotal(List<Tramo> tramos) {
        double total = tramos.stream()
            .mapToDouble(t -> t.getDistanciaKm() != null ? t.getDistanciaKm() : 0.0)
            .sum();

        return round2(total);
    }

    /**
     * Calcula la duración total en horas sumando la duración estimada
     * en segundos de cada tramo y convirtiéndola a horas (seg/3600).
     * El resultado se redondea a dos decimales.
     */
    private static Double calcularDuracionTotalHoras(List<Tramo> tramos) {
        double total = tramos.stream()
            .mapToDouble(t -> t.getDuracionEstimadaSegundos() != null ?
                    (t.getDuracionEstimadaSegundos() / 3600.0) : 0.0)
            .sum();

        return round2(total);
    }

    /**
     * Redondea un valor Double a dos decimales usando Math.round().
     */
    private static Double round2(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}