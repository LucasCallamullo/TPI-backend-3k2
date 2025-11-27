package com.tpi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tpi.dto.CostoFinalDTOs.CostoFinalDTO;
import com.tpi.dto.request.CrearRutaCompletaRequest;
import com.tpi.dto.response.CostosEstimadosDTOs.CostosEstimadosDTO;
import com.tpi.dto.response.RutaAsignadaResponseDTO;
import com.tpi.dto.response.RutasTramosCamionResponsesDTO.RutaTramosCamionResponse;
import com.tpi.dto.response.RutasTramosCamionResponsesDTO.TramoConDetalles;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Ruta;
import com.tpi.model.Tramo;
import com.tpi.model.Ubicacion;
import com.tpi.model.Tarifa;
import com.tpi.repository.RutaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RutaService {
    
    private final RutaRepository rutaRepository;

    private final TramoService tramoService;
    private final UbicacionService ubicacionService;
    private final TarifaService tarifaService;
    private final CalculoCostoService calculadoraService;

    @SuppressWarnings("null")
    public RutaAsignadaResponseDTO crearRutaParaSolicitud(CrearRutaCompletaRequest request) {

        log.info("Iniciando creación de ruta para solicitud ID: {}", request.getSolicitudId());
        
        // 1. Validaciones iniciales
        if (request.getDepositosIntermedios() == null) {
            log.debug("Depósitos intermedios es null, inicializando lista vacía");
            request.setDepositosIntermedios(new ArrayList<>());
        }
        log.debug("Cantidad de depósitos intermedios: {}", request.getDepositosIntermedios().size());

        // 2. buscar rutas para asociar
        log.debug("Buscando ubicación origen ID: {}", request.getOrigenId());
        Ubicacion origen = ubicacionService.findById(request.getOrigenId());
        log.debug("Ubicación origen encontrada: {}", origen.getId());
        
        log.debug("Buscando ubicación destino ID: {}", request.getDestinoId());
        Ubicacion destino = ubicacionService.findById(request.getDestinoId());
        log.debug("Ubicación destino encontrada: {}", destino.getId());

        // 3. buscar tarifa para asociar
        Tarifa tarifa = tarifaService.getTarifaByVolumenContenedor(request.getVolumenContenedor());

        log.debug("Tarifa encontrada: {}", tarifa.getId());

        // 4. Calcular cantidad de tramos
        int cantidadDepositos = request.getDepositosIntermedios().size();    // 1
        int cantidadTramos = cantidadDepositos + 1;
        log.debug("Cálculo de tramos: {} depósitos -> {} tramos", cantidadDepositos, cantidadTramos);

        // 5. Crear ruta
        log.debug("Creando entidad Ruta para solicitud ID: {}", request.getSolicitudId());
        Ruta ruta = Ruta.builder()
            .solicitudId(request.getSolicitudId())
            .tarifa(tarifa)
            .cantidadTramos(cantidadTramos)
            .cantidadDepositos(cantidadDepositos)
            .build();
        
        log.info("Guardando ruta en base de datos");
        ruta = rutaRepository.save(ruta);
        log.info("Ruta creada exitosamente con ID: {}", ruta.getId());

        // 6. Crear tramos
        log.debug("Iniciando creación automática de {} tramos", cantidadTramos);
        List<Tramo> tramos = tramoService.crearTramosAutomaticos(
            ruta, origen, destino, request.getDepositosIntermedios()
        );
        log.info("{} tramos creados exitosamente para ruta ID: {}", tramos.size(), ruta.getId());

        // 7. Retornar respuesta
        log.info("Creación de ruta completada para solicitud ID: {}. Ruta ID: {}", 
                 request.getSolicitudId(), ruta.getId());
        
        return RutaAsignadaResponseDTO.fromEntity(ruta, tarifa, tramos);
    }

    /**
     * Obtiene la información completa de una ruta asociada a una solicitud,
     * incluyendo sus tramos con detalles adicionales.
     *
     * @param solicitudId ID de la solicitud para la cual se desea obtener la ruta.
     * @return RutaTramosCamionResponse conteniendo la ruta y sus tramos con detalles.
     * @throws EntityNotFoundException si no existe una ruta asociada al ID de solicitud dado.
     */
    public RutaTramosCamionResponse obtenerRutaConTramosPorSolicitudId(Long solicitudId) {

        // 1. Buscar la ruta asociada al ID de solicitud en la base de datos.
        //    Si no existe, se lanza EntityNotFoundException.
        Ruta ruta = rutaRepository.findBySolicitudId(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "No se encontró ruta para la solicitud ID: " + solicitudId
                ));

        // 2. Obtener todos los tramos pertenecientes a esta ruta,
        //    incluyendo información detallada (camión, origen, destino, etc.)
        List<TramoConDetalles> tramos = tramoService.obtenerTramosConDetallesPorRutaId(ruta.getId());

        // 3. Construir el DTO de respuesta unificado con la ruta + tramos.
        return RutaTramosCamionResponse.of(ruta, tramos);
    }


    /**
     * Calcula los costos estimados para una solicitud específica.
     *
     * @param solicitudId ID de la solicitud cuya ruta se usará para calcular costos.
     * @return DTO con los costos estimados calculados.
     * @throws EntidadNotFoundException si no existe una ruta asociada a esa solicitud.
     */
    public CostosEstimadosDTO calcularGastosEstimados(Long solicitudId) {

        // 1. Buscar la ruta asociada a la solicitud.
        //    Si no existe, se lanza una excepción personalizada.
        Ruta ruta = rutaRepository.findBySolicitudId(solicitudId)
            .orElseThrow(() -> new EntidadNotFoundException("Ruta", solicitudId));

        // 2. Delegar el cálculo a la clase encargada del cálculo de costos.
        return calculadoraService.calcularCostosEstimados(ruta);
    }


    /**
     * Calcula los costos totales (finales) de una ruta asociada a una solicitud.
     *
     * @param solicitudId ID de la solicitud cuya ruta se evaluará.
     * @return DTO con el costo final de la ruta.
     * @throws EntidadNotFoundException si no existe una ruta asociada a esa solicitud.
     */
    public CostoFinalDTO calcularGastosTotales(Long solicitudId) {

        // 1. Buscar la ruta vinculada a la solicitud.
        //    Si no existe, se lanza una excepción indicando el problema.
        Ruta ruta = rutaRepository.findBySolicitudId(solicitudId)
            .orElseThrow(() -> new EntidadNotFoundException("Ruta", solicitudId));

        // 2. Calcular los costos finales con el servicio especializado.
        return calculadoraService.calcularCostoFinalRuta(ruta);
    }



    /**
     * Obtiene todas las rutas disponibles en la base de datos.
     *
     * @return Lista completa de rutas.
     */
    public List<Ruta> findAll() {
        // 1. Solicita al repositorio que devuelva todas las rutas almacenadas.
        return rutaRepository.findAll();
    }
    
    /**
     * Busca una ruta por su ID.
     *
     * @param id ID de la ruta a buscar.
     * @return La Ruta encontrada.
     * @throws EntidadNotFoundException si no existe una ruta con ese ID.
     */
    @SuppressWarnings("null")
    public Ruta findById(Long id) {
        // 1. Intenta buscar la ruta por ID.
        // 2. Si no existe, lanza una excepción personalizada indicando que no se encontró.
        return rutaRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Ruta", id));
    }
    
    /**
     * Guarda una ruta en la base de datos (crear o actualizar).
     *
     * @param e Ruta a persistir.
     * @return La ruta guardada con su ID generado (si es nueva).
     */
    @SuppressWarnings("null")
    public Ruta save(Ruta e) {
        // 1. Envía la ruta al repositorio para persistirla.
        // 2. Devuelve la entidad ya almacenada o actualizada.
        return rutaRepository.save(e);
    }
}