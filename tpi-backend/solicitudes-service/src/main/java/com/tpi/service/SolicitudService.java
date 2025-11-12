package com.tpi.service;

import com.tpi.model.Solicitud;
import com.tpi.model.Contenedor;
import com.tpi.model.EstadoSolicitud;
import com.tpi.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ContenedorService contenedorService;
    private final EstadoSolicitudService estadoSolicitudService;

    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Solicitud> findById(Long id) {
        return solicitudRepository.findById(id);
    }

    public List<Solicitud> findByClienteId(String clienteId) {
        return solicitudRepository.findByClienteId(clienteId);
    }

    public List<Solicitud> findByEstado(String estadoNombre) {
        return solicitudRepository.findByEstadoNombre(estadoNombre);
    }

    @SuppressWarnings("null")
    public Solicitud save(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        solicitudRepository.deleteById(id);
    }

    /**
     * Crea una nueva solicitud de transporte (flujo principal)
     */
    @SuppressWarnings("null")
    public Solicitud crearSolicitud(String clienteId, Double peso, Double volumen, 
                                  String identificacionUnica, String descripcion) {
        
        // 1. Crear o validar contenedor
        Contenedor contenedor = contenedorService.crearContenedor(peso, volumen, identificacionUnica);
        
        // 2. Obtener estado inicial "BORRADOR"
        EstadoSolicitud estadoBorrador = estadoSolicitudService.findByNombre("BORRADOR")
            .orElseThrow(() -> new RuntimeException("Estado BORRADOR no encontrado"));

        // 3. Crear solicitud
        Solicitud solicitud = Solicitud.builder()
            .clienteId(clienteId)
            .contenedor(contenedor)
            .estado(estadoBorrador)
            .costoEstimado(BigDecimal.ZERO)
            .tiempoEstimado(0)
            .build();

        return solicitudRepository.save(solicitud);
    }

    /**
     * Cambia el estado de una solicitud
     */
    public Solicitud cambiarEstado(Long solicitudId, String nuevoEstadoNombre) {
        @SuppressWarnings("null")
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        EstadoSolicitud nuevoEstado = estadoSolicitudService.findByNombre(nuevoEstadoNombre)
            .orElseThrow(() -> new RuntimeException("Estado " + nuevoEstadoNombre + " no encontrado"));

        solicitud.setEstado(nuevoEstado);
        return solicitudRepository.save(solicitud);
    }

    /**
     * Actualiza costos y tiempos estimados
     */
    public Solicitud actualizarEstimaciones(Long solicitudId, BigDecimal costoEstimado, Integer tiempoEstimado) {
        @SuppressWarnings("null")
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setCostoEstimado(costoEstimado);
        solicitud.setTiempoEstimado(tiempoEstimado);

        return solicitudRepository.save(solicitud);
    }

    /**
     * Registra costos y tiempos reales al finalizar
     */
    public Solicitud finalizarSolicitud(Long solicitudId, BigDecimal costoFinal, Integer tiempoReal) {
        @SuppressWarnings("null")
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setCostoFinal(costoFinal);
        solicitud.setTiempoReal(tiempoReal);

        // Cambiar estado a ENTREGADA
        EstadoSolicitud estadoEntregada = estadoSolicitudService.findByNombre("ENTREGADA")
            .orElseThrow(() -> new RuntimeException("Estado ENTREGADA no encontrado"));
        solicitud.setEstado(estadoEntregada);

        return solicitudRepository.save(solicitud);
    }

    /**
     * Valida si ya existe una solicitud con el mismo contenedor
     */
    public boolean existeSolicitudConContenedor(String identificacionUnica) {
        return solicitudRepository.existsByContenedorIdentificacionUnica(identificacionUnica);
    }
}