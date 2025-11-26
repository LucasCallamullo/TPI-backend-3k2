package com.tpi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tpi.client.RoutingClient;
import com.tpi.client.SolicitudClient;

import com.tpi.dto.external.ContenedorResponseDTO;
import com.tpi.dto.response.RouteResponse;
import com.tpi.dto.response.RutasTramosCamionResponsesDTO.TramoConDetalles;

import com.tpi.exception.EntidadNotFoundException;

import com.tpi.model.Camion;
import com.tpi.model.Deposito;
import com.tpi.model.EstadoTramo;
import com.tpi.model.Ruta;
import com.tpi.model.TipoTramo;
import com.tpi.model.Tramo;
import com.tpi.model.Ubicacion;
import com.tpi.repository.TramoRepository;


@Service
@RequiredArgsConstructor
public class TramoService {

    private final TramoRepository tramoRepository;
    private final DepositoService depositoService;
    private final TipoTramoService tipoTramoService;
    private final EstadoTramoService estadoTramoService;
    private final CamionService camionService;

    private final RoutingClient routingClient;
    private final SolicitudClient solicitudClient;

    /**
     * Genera automáticamente los tramos de una ruta en función de:
     * - Origen
     * - Destino
     * - Lista opcional de depósitos intermedios
     *
     * Lógica general:
     *  - Si NO hay depósitos → un único tramo ORIGEN → DESTINO
     *  - Si hay depósitos → se generan tantos tramos como segmentos existan:
     *        Origen → Depósito1
     *        Depósito1 → Depósito2
     *        ...
     *        ÚltimoDepósito → Destino
     */
    public List<Tramo> crearTramosAutomaticos(
            Ruta ruta, Ubicacion origen, Ubicacion destino, List<Long> depositosIds) {
        
        // Paso 1: Crear contenedor para los tramos que se devolverán
        List<Tramo> tramos = new ArrayList<>();

        // Paso 2: Obtener depósitos si la lista contiene IDs
        List<Deposito> depositos = (depositosIds != null && !depositosIds.isEmpty())
            ? depositoService.findAllById(depositosIds)
            : new ArrayList<>();

        // Paso 3: Extraer ubicaciones de cada depósito
        List<Ubicacion> ubicacionesDepositos = depositos.stream()
            .map(Deposito::getUbicacion)
            .collect(Collectors.toList());

        // ============================================================
        // Caso 1: NO hay depósitos intermedios → tramo directo
        // ============================================================
        // Paso 1: Determinar estado inicial del tramo (ASiGNADO)
        EstadoTramo estado = estadoTramoService.findByNombre("ESTIMADO");

        if (ubicacionesDepositos.isEmpty()) {

            // Paso 4: Obtener tipo de tramo ORIGEN_DESTINO
            TipoTramo tipoTramo = tipoTramoService.findByNombre("ORIGEN_DESTINO");

            // Paso 5: Crear tramo directo origen → destino (orden = 0)
            Tramo tramoDirecto = crearTramo(
                ruta, origen, destino,
                tipoTramo, 0, estado
            );

            // Paso 6: Guardar tramo directo en la lista
            tramos.add(tramoDirecto);
        }
        // ============================================================
        // Caso 2: Sí hay depósitos intermedios
        // ============================================================
        else {
            // Paso 7: Primer tramo Origen → Primer depósito
            TipoTramo tipoTramo = tipoTramoService.findByNombre("ORIGEN_DEPOSITO");

            Tramo primerTramo = crearTramo(
                ruta, origen, ubicacionesDepositos.get(0),
                tipoTramo, 0, estado
            );
            tramos.add(primerTramo);

            // Paso 8: Tramos intermedios entre depósitos consecutivos
            for (int i = 0; i < ubicacionesDepositos.size() - 1; i++) {

                TipoTramo tipoTramito = tipoTramoService.findByNombre("DEPOSITO_DEPOSITO");

                Tramo tramoIntermedio = crearTramo(
                    ruta, ubicacionesDepositos.get(i), ubicacionesDepositos.get(i + 1),
                    tipoTramito, i + 1, estado
                );

                tramos.add(tramoIntermedio);
            }

            // Paso 9: Último tramo ÚltimoDepósito → Destino
            TipoTramo tipoTramoLast = tipoTramoService.findByNombre("DEPOSITO_DESTINO");

            Tramo ultimoTramo = crearTramo(
                ruta,
                ubicacionesDepositos.get(ubicacionesDepositos.size() - 1),
                destino,
                tipoTramoLast,
                ubicacionesDepositos.size(),
                estado
            );

            tramos.add(ultimoTramo);
        }

        // Paso 10: Guardar todos los tramos generados en la BD
        return tramoRepository.saveAll(tramos);
    }


    /**
     * Crea un Tramo entre dos ubicaciones (origen → destino) calculando
     * distancia y duración con el servicio de routing (OSRM). Si falla el
     * llamado al servicio, usa un cálculo de fallback euclidiano.
     *
     * @param ruta      Ruta a la que pertenece el tramo (referencia).
     * @param origen    Ubicación de origen.
     * @param destino   Ubicación de destino.
     * @param tipoTramo Tipo de tramo (ORIGEN_DESTINO, DEPOSITO_DEPOSITO, etc.).
     * @param orden     Posición/orden del tramo dentro de la ruta.
     * @param estado     Estado de tramo ("ESTIMADO", "ASIGNADO", "INICIADO", "FINALIZADO")
     * @return Tramo construido (sin persistir).
     */
    private Tramo crearTramo(Ruta ruta, Ubicacion origen, Ubicacion destino,
                            TipoTramo tipoTramo, int orden, EstadoTramo estado) {

        // Paso 1: Intentar calcular distancia y duración usando el servicio de routing (OSRM)
        RouteResponse rutaInfo;
        try {
            // 2 Llamada al cliente de routing pasando lat/lon de origen y destino
            rutaInfo = routingClient.calcularRutaCompleta(
                origen.getLatitud(), origen.getLongitud(),
                destino.getLatitud(), destino.getLongitud()
            );
        } catch (Exception e) {
            // Paso 3: Si falla el servicio externo, aplicar fallback simple
            // 3.1 Calcular distancia euclidiana aproximada (en km)
            double distanciaFallback = calcularDistanciaEuclidiana(origen, destino);

            // 3.2 Construir un RouteResponse mínimo con la información disponible
            //      - distancia: aproximada (km)
            //      - duracion: 0 (desconocida, se puede mejorar según criterio)
            //      - dejamos lat/lon tal cual para trazabilidad
            rutaInfo = new RouteResponse(
                distanciaFallback,
                0L,
                origen.getLatitud(), origen.getLongitud(),
                destino.getLatitud(), destino.getLongitud()
            );
        }

        // Paso 4: Construir y devolver el Tramo usando los valores calculados
        double distanciaRedondeada = Math.round(rutaInfo.distanciaKm() * 100.0) / 100.0;

        return Tramo.builder()
            .ruta(ruta) // 4.1 Referencia a la ruta (por entidad/ID)
            .origen(origen) // 4.2 Origen (entidad)
            .destino(destino) // 4.3 Destino (entidad)
            .tipo(tipoTramo) // 4.4 Tipo de tramo
            .estado(estado) // 4.5 Estado inicial
            .distanciaKm(distanciaRedondeada) // 4.6 Distancia en km (desde OSRM o fallback)
            .duracionEstimadaSegundos(rutaInfo.duracionSegundos()) // 4.7 Duración estimada en segundos INT
            .orden(orden) // 4.8 Orden para mantener secuencia de tramos
            .build();
    }


    /**
     * Método de fallback: aproximación euclidiana entre dos puntos (lat/lon).
     * Nota: Multiplicamos por ~111 para convertir grados de latitud/longitud a km
     * (aprox. 1 grado ≈ 111 km). Es una aproximación válida para estimaciones rápidas.
     *
     * @param origen  Ubicación de origen.
     * @param destino Ubicación de destino.
     * @return Distancia aproximada en kilómetros.
     */
    private Double calcularDistanciaEuclidiana(Ubicacion origen, Ubicacion destino) {
        // Paso 1: Diferencias en latitud y longitud (grados)
        double latDiff = destino.getLatitud() - origen.getLatitud();
        double lonDiff = destino.getLongitud() - origen.getLongitud();

        // Paso 2: Distancia euclidiana en grados -> luego convertida a km (~111 km/grado)
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111;
    }


    /**
     * Obtiene una lista de tramos con sus detalles completos para una ruta específica.
     *
     * @param rutaId ID de la ruta cuyos tramos se desean obtener.
     * @return Lista de objetos TramoConDetalles ordenados por su campo "orden".
     */
    public List<TramoConDetalles> obtenerTramosConDetallesPorRutaId(Long rutaId) {
        List<Tramo> tramos = tramoRepository.findByRutaIdOrderByOrdenAsc(rutaId);
        
        return tramos.stream()
                .map(this::mapearTramoConDetalles)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Tramo en un DTO TramoConDetalles.
     *
     * @param tramo Entidad Tramo a transformar.
     * @return DTO TramoConDetalles correspondiente.
     */
    private TramoConDetalles mapearTramoConDetalles(Tramo tramo) {
        return TramoConDetalles.of(tramo);
    }

    /**
     * Obtiene únicamente las entidades Tramo para una ruta específica,
     * sin mapearlas a DTOs.
     *
     * @param rutaId ID de la ruta.
     * @return Lista de entidades Tramo ordenadas por "orden".
     */
    public List<Tramo> tramosPorRutaId(Long rutaId){
        return tramoRepository.findByRutaIdOrderByOrdenAsc(rutaId);
    }


    /**
     * Obtiene un tramo por su ID.
     *
     * @param id identificador único del tramo
     * @return Tramo encontrado
     * @throws EntidadNotFoundException si no existe un tramo con el ID dado
     */
    @SuppressWarnings("null")
    public Tramo getById(Long id) {
        return tramoRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("Tramo no encotrado con id: ", id));
    }

    /**
     * Guarda o actualiza un tramo en la base de datos.
     *
     * @param tramo entidad Tramo a persistir
     * @return Tramo guardado o actualizado
     */
    @SuppressWarnings("null")
    public Tramo save(Tramo tramo) {
        return tramoRepository.save(tramo);
    }

    /**
     * Obtiene todos los tramos registrados.
     *
     * @return lista de todos los tramos
     */
    public List<Tramo> findAll() {
        return tramoRepository.findAll();
    }

    /**
     * Asigna un camión existente a un tramo específico.
     *
     * @param tramoId ID del tramo al cual se asignará el camión
     * @param camionId ID del camión que será asignado
     * @return TramoConDetalles DTO con la información del tramo actualizado
     * @throws EntidadNotFoundException si el tramo o camión no existen
     */
    public TramoConDetalles asignarCamionATramo(Long tramoId, Long camionId) {
        // 1. Obtener el tramo
        Tramo tramo = this.getById(tramoId);

        // 2. Obtener la ruta asociada al tramo
        Ruta ruta = tramo.getRuta();

        // 3. Consultar información del contenedor desde el microservicio de solicitudes
        ContenedorResponseDTO contenedorDTO = solicitudClient.obtenerInfoContenedor(ruta.getSolicitudId());

        // 4. Obtener el camión
        Camion camion = camionService.findById(camionId);

        // 5. Validar capacidad (peso y volumen) sino propaga error
        camionService.validarCapacidadVolumenYPeso(camion, contenedorDTO);

        // 6. Asignar camión al tramo

        // Paso 6.1: Determinar estado inicial del tramo (ASiGNADO)
        EstadoTramo estado = estadoTramoService.findByNombre("ASIGNADO");
        tramo.setCamion(camion);
        tramo.setEstado(estado);

        // 7. Persistir cambios
        tramoRepository.save(tramo);

        // 8. Devolver DTO con detalles
        return TramoConDetalles.of(tramo);
    }


}
