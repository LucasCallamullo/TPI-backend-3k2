package com.tpi.dto.response;

import com.tpi.model.TipoUbicacion;
import com.tpi.model.Ubicacion;
import io.swagger.v3.oas.annotations.media.Schema;

public class UbicacionDTOs {
    
    @Schema(description = "DTO para respuesta de ubicaciones")
    public record UbicacionResponseDTO(
        Long id,
        String direccion,
        String nombre,
        Double latitud,
        Double longitud,
        TipoUbicacionDTO tipo
    ) {
        public static UbicacionResponseDTO fromEntity(Ubicacion ubicacion) {

            TipoUbicacion tipo = ubicacion.getTipo();
            TipoUbicacionDTO tipoDto = tipo != null ? 
                new TipoUbicacionDTO(tipo.getId(), tipo.getNombre()) : null;

            return new UbicacionResponseDTO(
                ubicacion.getId(),
                ubicacion.getDireccion(),
                ubicacion.getNombre(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                tipoDto
            );
        }
    }

    @Schema(description = "DTO para información del tipo de ubicación")
    public record TipoUbicacionDTO(
        @Schema(description = "ID del tipo de ubicación", example = "1")
        Long id,
        
        @Schema(description = "Nombre del tipo de ubicación")
        String nombre
    ) {}
}