package com.tpi.dto.external;

import java.util.Date;
import java.util.List;

public class CostoFinalDTOs {

    public record CostoFinalDTO(
        Long rutaId,
        Date fechaCalculo,

        ResumenCfDTO resumen,
        CostosCfDTO costos,

        // Lista de tramos con su costo individual
        List<TramoCfDTO> tramos
    ) {}

    // ---------- Subcomponentes ----------

    public record ResumenCfDTO(
        Integer cantidadTramos,
        Double costoTotal,
        Double distanciaTotalKm,
        Double totalHoras
    ) {}

    public record CostosCfDTO(
        Double gestion,
        Double camion,
        Double combustibleTotal,
        Double combustibleLitro,
        Double estadia,
        Double costoTotal
    ) {}

    public record TramoCfDTO(
        Long tramoId,
        Double distanciaKm,
        Double duracionHoras,
        Double costoReal,
        Boolean involucraEstadia,

        // Info del camión asignado (compacta)
        CamionCfDTO camion
    ) {}

    public record CamionCfDTO(
        Long id,
        String dominio,
        Double costoPorKm,
        Double consumoLx100
    ) {}
}
