package com.tpi.service;

import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.EstadoSolicitud;
import com.tpi.repository.EstadoSolicitudRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoSolicitudService {

    private final EstadoSolicitudRepository estadoSolicitudRepository;

    /**
     * Devuelve todos los estados de solicitud existentes.
     *
     * @return Lista de todos los estados de solicitud.
     */
    public List<EstadoSolicitud> findAll() {
        return estadoSolicitudRepository.findAll();
    }

    /**
     * Busca un estado de solicitud por su ID.
     *
     * @param id ID del estado a buscar.
     * @return EstadoSolicitud encontrado.
     * @throws EntidadNotFoundException si no existe un estado con el ID dado.
     */
    @SuppressWarnings("null")
    public EstadoSolicitud findById(Long id) {
        return estadoSolicitudRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException(
                "EstadoSolicitud", 
                "No se encontró el estado con nombre: " + id
            ));
    }

    /**
     * Busca un estado de solicitud por su nombre.
     *
     * @param nombre Nombre del estado a buscar.
     * @return EstadoSolicitud encontrado.
     * @throws EntidadNotFoundException si no existe un estado con el nombre dado.
     */
    public EstadoSolicitud findByNombre(String nombre) {
        return estadoSolicitudRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException(
                "EstadoSolicitud", 
                "No se encontró el estado con nombre: " + nombre
            ));
    }

    /**
     * Guarda un estado de solicitud en la base de datos.
     *
     * @param estadoSolicitud Estado a guardar.
     * @return EstadoSolicitud guardado.
     */
    @SuppressWarnings("null")
    public EstadoSolicitud save(EstadoSolicitud estadoSolicitud) {
        return estadoSolicitudRepository.save(estadoSolicitud);
    }

    /**
     * Elimina un estado de solicitud por su ID.
     *
     * @param id ID del estado a eliminar.
     */
    @SuppressWarnings("null")
    public void deleteById(Long id) {
        estadoSolicitudRepository.deleteById(id);
    }

    /**
     * Verifica si existe un estado de solicitud con un nombre dado.
     *
     * @param nombre Nombre a verificar.
     * @return true si existe un estado con ese nombre, false en caso contrario.
     */
    public boolean existsByNombre(String nombre) {
        return estadoSolicitudRepository.existsByNombre(nombre);
    }
}