package com.tpi.service;

import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.EstadoContenedor;
import com.tpi.repository.EstadoContenedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoContenedorService {

    private final EstadoContenedorRepository estadoContenedorRepository;

    /**
     * Devuelve todos los estados de contenedor existentes.
     *
     * @return Lista de todos los estados de contenedor.
     */
    public List<EstadoContenedor> findAll() {
        return estadoContenedorRepository.findAll();
    }

    /**
     * Busca un estado de contenedor por su ID.
     *
     * @param id ID del estado a buscar.
     * @return EstadoContenedor encontrado.
     * @throws EntidadNotFoundException si no existe un estado con el ID dado.
     */
    @SuppressWarnings("null")
    public EstadoContenedor findById(Long id) {
        return estadoContenedorRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException(
                "EstadoSolicitud", 
                "No se encontró el estado con nombre: " + id
            ));
    }

    /**
     * Busca un estado de contenedor por su nombre.
     *
     * @param nombre Nombre del estado a buscar.
     * @return EstadoContenedor encontrado.
     * @throws EntidadNotFoundException si no existe un estado con el nombre dado.
     */
    public EstadoContenedor findByNombre(String nombre) {
        return estadoContenedorRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException(
                "EstadoSolicitud", 
                "No se encontró el estado con nombre: " + nombre
            ));
    }

    /**
     * Guarda un estado de contenedor en la base de datos.
     *
     * @param estadoContenedor Estado a guardar.
     * @return EstadoContenedor guardado.
     */
    @SuppressWarnings("null")
    public EstadoContenedor save(EstadoContenedor estadoContenedor) {
        return estadoContenedorRepository.save(estadoContenedor);
    }

    /**
     * Elimina un estado de contenedor por su ID.
     *
     * @param id ID del estado a eliminar.
     */
    @SuppressWarnings("null")
    public void deleteById(Long id) {
        estadoContenedorRepository.deleteById(id);
    }

    /**
     * Verifica si existe un estado de contenedor con un nombre dado.
     *
     * @param nombre Nombre a verificar.
     * @return true si existe un estado con ese nombre, false en caso contrario.
     */
    public boolean existsByNombre(String nombre) {
        return estadoContenedorRepository.existsByNombre(nombre);
    }
}