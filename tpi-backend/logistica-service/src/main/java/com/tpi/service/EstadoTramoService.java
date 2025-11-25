package com.tpi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.EstadoTramo;
import com.tpi.repository.EstadoTramoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstadoTramoService {

    private final EstadoTramoRepository estadoTramoRepository;
    
    /**
     * Obtiene todos los estados de tramo disponibles.
     *
     * @return lista con todos los EstadoTramo.
     */
    public List<EstadoTramo> findAll() {
        return estadoTramoRepository.findAll();
    }

    /**
     * Busca un estado de tramo por su ID.
     * Si no existe, lanza EntidadNotFoundException.
     *
     * @param id identificador del estado de tramo.
     * @return el EstadoTramo encontrado.
     */
    @SuppressWarnings("null")
    public EstadoTramo findById(Long id) {
        return estadoTramoRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("EstadoTramo", id));
    }

    /**
     * Guarda un nuevo estado de tramo o actualiza uno existente.
     *
     * @param estadoTramo entidad a persistir.
     * @return la entidad guardada.
     */
    @SuppressWarnings("null")
    public EstadoTramo save(EstadoTramo estadoTramo) {
        return estadoTramoRepository.save(estadoTramo);
    }

    /**
     * Busca un estado de tramo por su nombre.
     * Si no existe, lanza EntidadNotFoundException.
     *
     * @param nombre nombre del estado de tramo.
     * @return el EstadoTramo correspondiente.
     */
    public EstadoTramo findByNombre(String nombre) {
        return estadoTramoRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException("EstadoTramo", nombre));
    }
}