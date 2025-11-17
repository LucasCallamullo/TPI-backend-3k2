package com.tpi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tpi.client.RoutingClient;
import com.tpi.dto.response.RouteResponse;
import com.tpi.dto.response.RutasTramosCamionResponsesDTO.TramoConDetalles;
import com.tpi.model.Deposito;
import com.tpi.model.EstadoTramo;
import com.tpi.model.Ruta;
import com.tpi.model.TipoTramo;
import com.tpi.model.Tramo;
import com.tpi.model.Ubicacion;
import com.tpi.repository.TramoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoService {

    private final TramoRepository tramoRepository;
    private final DepositoService depositoService;
    private final TipoTramoService tipoTramoService;
    private final EstadoTramoService estadoTramoService;
    private final RoutingClient routingClient;

    /**
     * En este punto la logica es los tramos estan dados por el origen y el destino
     * en caos de que el origen vaya directo a destino es cuando no hay depositos intermedios
     * 
     * sino se calculara en caso de un deposito
     * origien - deposito  -->>  deposito - destitno
     * 
     * y asi sucesivamente en base a la cantidad de depositos intermedios
     */
    public List<Tramo> crearTramosAutomaticos(
        Ruta ruta, Ubicacion origen, Ubicacion destino, List<Long> depositosIds) {
        
        List<Tramo> tramos = new ArrayList<>();
        // Obtener depósitos (puede estar vacío)
        List<Deposito> depositos = (depositosIds != null && !depositosIds.isEmpty()) 
            ? depositoService.findAllById(depositosIds) 
            : new ArrayList<>();
        
        List<Ubicacion> ubicacionesDepositos = depositos.stream()
            .map(Deposito::getUbicacion)
            .collect(Collectors.toList());
        
        // Caso 1: Sin depósitos intermedios (origen → destino directo)
        if (ubicacionesDepositos.isEmpty()) {
            TipoTramo tipoTramo = tipoTramoService.findByNombre("ORIGEN_DESTINO");

            Tramo tramoDirecto = crearTramo(
                ruta, origen, destino, 
                tipoTramo, 0
            );
            tramos.add(tramoDirecto);
        } 
        // Caso 2: Con depósitos intermedios
        else {
            TipoTramo tipoTramo = tipoTramoService.findByNombre("ORIGEN_DEPOSITO");

            // Primer tramo: Origen → Primer depósito
            Tramo primerTramo = crearTramo(
                ruta, origen, ubicacionesDepositos.get(0),
                tipoTramo, 0
            );
            tramos.add(primerTramo);
            
            // Tramos intermedios: Depósito → Depósito
            for (int i = 0; i < ubicacionesDepositos.size() - 1; i++) {
                TipoTramo tipoTramito = tipoTramoService.findByNombre("DEPOSITO_DEPOSITO");

                Tramo tramoIntermedio = crearTramo(
                    ruta, ubicacionesDepositos.get(i), ubicacionesDepositos.get(i + 1),
                    tipoTramito, i + 1
                );
                tramos.add(tramoIntermedio);
            }
            
            // Último tramo: Último depósito → Destino
            TipoTramo tipoTramoLast = tipoTramoService.findByNombre("DEPOSITO_DESTINO");

            Tramo ultimoTramo = crearTramo(
                ruta, ubicacionesDepositos.get(ubicacionesDepositos.size() - 1), destino,
                tipoTramoLast, ubicacionesDepositos.size()
            );
            tramos.add(ultimoTramo);
        }
        
        return tramoRepository.saveAll(tramos);
    }

    private Tramo crearTramo(Ruta ruta, Ubicacion origen, Ubicacion destino, 
                           TipoTramo tipoTramo, int orden) {
           
        // EStado del tramo     // ESTIMADO, ASIGNADO, INICIADO, FINALIZADO
        EstadoTramo estado = estadoTramoService.findByNombre("ESTIMADO");
        
        // Calcular distancia con Google Maps API
        // Ahora usas tu OSRM en Docker
        RouteResponse rutaInfo;
            try {
                rutaInfo = routingClient.calcularRutaCompleta(
                    origen.getLatitud(), origen.getLongitud(),
                    destino.getLatitud(), destino.getLongitud()
                );
            } catch (Exception e) {
                // Fallback a cálculo simple
                double distanciaFallback = calcularDistanciaEuclidiana(origen, destino);
                rutaInfo = new RouteResponse(distanciaFallback, 0L, 
                                        origen.getLatitud(), origen.getLongitud(),
                                        destino.getLatitud(), destino.getLongitud());
            }
            
            return Tramo.builder()
                .ruta(ruta) // Solo referencia por ID
                .origen(origen)
                .destino(destino)
                .tipo(tipoTramo)
                .estado(estado)
                .distanciaKm(rutaInfo.distanciaKm())
                .duracionEstimadaSegundos(rutaInfo.duracionSegundos())    // para calculo posterior de estimado en horas
                .orden(orden) // Para mantener el orden de los tramos
                .fechaHoraCreacion(new Date())
                .build();
        }

        // Método de fallback para cálculo de distancia simple
        private Double calcularDistanciaEuclidiana(Ubicacion origen, Ubicacion destino) {
            double latDiff = destino.getLatitud() - origen.getLatitud();
            double lonDiff = destino.getLongitud() - origen.getLongitud();
            return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111; // Aproximación a km
        }


    public List<TramoConDetalles> obtenerTramosConDetallesPorRutaId(Long rutaId) {
        List<Tramo> tramos = tramoRepository.findByRutaIdOrderByOrdenAsc(rutaId);
        
        return tramos.stream()
                .map(this::mapearTramoConDetalles)
                .collect(Collectors.toList());
    }

    private TramoConDetalles mapearTramoConDetalles(Tramo tramo) {
        return TramoConDetalles.of(tramo);
    }

    /*
     * encuentra tramos por repository
     */
    public List<Tramo> tramosPorRutaId(Long rutaId){
        return tramoRepository.findByRutaIdOrderByOrdenAsc(rutaId);
    }
}
