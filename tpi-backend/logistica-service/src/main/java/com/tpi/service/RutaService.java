package com.tpi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tpi.dto.CostoFinalDTO;
import com.tpi.dto.request.CrearRutaCompletaRequest;
import com.tpi.dto.response.CostosEstimadosDTO;
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
        log.debug("Buscando tarifa ID: {}", request.getTarifaId());
        Tarifa tarifa = tarifaService.findById(request.getTarifaId());
        log.debug("Tarifa encontrada: {}", tarifa.getId());

        // 4. Calcular cantidad de tramos
        int cantidadDepositos = request.getDepositosIntermedios().size();
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

    /*
     * Obtener ruta y tramos para el seguimiento
     */
    public RutaTramosCamionResponse obtenerRutaConTramosPorSolicitudId(Long solicitudId) {
        // Buscar la ruta por solicitudId
        Ruta ruta = rutaRepository.findBySolicitudId(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "No se encontró ruta para la solicitud ID: " + solicitudId
                ));

        // Obtener los tramos de esta ruta
        List<TramoConDetalles> tramos = tramoService.obtenerTramosConDetallesPorRutaId(ruta.getId());

        return RutaTramosCamionResponse.of(ruta, tramos);
    }


    /*
     * Se encarga de calcular un gasto estimado
     */
    @SuppressWarnings("null")
    public CostosEstimadosDTO calcularGastosEstimados(Long solicitudId) {
        Ruta ruta = rutaRepository.findById(solicitudId)
            .orElseThrow( () -> new EntidadNotFoundException("Ruta", solicitudId));

        return calculadoraService.calcularCostosEstimados(ruta);
    }


    /*
     * Se encarga de calcular un gastos totales
     */
    @SuppressWarnings("null")
    public CostoFinalDTO calcularGastosTotales(Long solicitudId) {
        Ruta ruta = rutaRepository.findById(solicitudId)
            .orElseThrow( () -> new EntidadNotFoundException("Ruta", solicitudId));

        return calculadoraService.calcularCostoFinalRuta(ruta);
    }




    public List<Ruta> findAll() {
        return rutaRepository.findAll();
    }
    
    @SuppressWarnings("null")
    public Ruta findById(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Ruta", id));
    }
    
    @SuppressWarnings("null")
    public Ruta save(Ruta e) {
        return rutaRepository.save(e);
    }
}