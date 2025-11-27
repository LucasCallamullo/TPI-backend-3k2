package com.tpi.dto;

import java.util.Date;
import java.util.List;

import com.tpi.model.Camion;
import com.tpi.model.Ruta;
import com.tpi.model.Tramo;

public class CostoFinalDTOs {

    public record CostoFinalDTO(
        Long rutaId,
        Date fechaCalculo,

        ResumenCfDTO resumen,
        CostosCfDTO costos,

        // Lista de tramos con su costo individual
        List<TramoCfDTO> tramos
    ) {

        public static CostoFinalDTO of(
            Ruta ruta,
            ResumenCfDTO resumen,
            CostosCfDTO costos,
            List<Tramo> tramos
        ) {

            List<TramoCfDTO> tramosDto = tramos.stream()
                .map(TramoCfDTO::fromEntity)
                .toList();

            return new CostoFinalDTO(
                ruta.getId(),
                new Date(),
                resumen,
                costos,
                tramosDto
            );
        }
    }

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
    ) {

        public static TramoCfDTO fromEntity(Tramo tramo) {

            Double horas = tramo.getDuracionEstimadaSegundos() / 3600.0;
            Camion camion = tramo.getCamion();

            CamionCfDTO camionDto = new CamionCfDTO(
                camion.getId(), 
                camion.getDominio(), 
                camion.getCostoPorKm(), 
                camion.getConsumoCombustibleLx100km()
            );

            return new TramoCfDTO(
                tramo.getId(),
                tramo.getDistanciaKm(),
                horas,
                tramo.getCostoReal(),
                tramo.involucraEstadiaEnDeposito(),
                camionDto
            );
        }
    }

    public record CamionCfDTO(
        Long id,
        String dominio,
        Double costoPorKm,
        Double consumoLx100
    ) {}
}

/* /
@Data
@Builder
public class CostoFinalDTO {
    private Long rutaId;
    private Integer cantidadTramos;
    
    // DESGLOSE DE COSTOS
    private Double costoGestion;
    private Double costoCamion;
    private Double costoCombustible;
    private Double costoEstadia;
    private Double costoTotal;
    private Long tiempoTotalSegundos;
    
    // Método helper para formatear
    public String getResumen() {
        return String.format(
            "Ruta %d - %d tramos: Gestión: $%.2f, Camión: $%.2f, Combustible: $%.2f, Estadía: $%.2f, TOTAL: $%.2f",
            rutaId, cantidadTramos, costoGestion, costoCamion, costoCombustible, costoEstadia, costoTotal
        );
    }
}
*/