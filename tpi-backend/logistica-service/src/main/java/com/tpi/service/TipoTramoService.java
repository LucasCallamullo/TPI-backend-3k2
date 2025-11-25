package com.tpi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.repository.TipoTramoRepository;
import com.tpi.model.TipoTramo;

@Service
@RequiredArgsConstructor
public class TipoTramoService {

    private final TipoTramoRepository tipoTramoRepository;
    
    /**
     * Devuelve todos los tipos de tramo existentes.
     *
     * @return Lista de todos los tipos de tramo.
     */
    public List<TipoTramo> findAll() {
        return tipoTramoRepository.findAll();
    }

    /**
     * Busca un tipo de tramo por su ID.
     *
     * @param id ID del tipo de tramo a buscar.
     * @return TipoTramo encontrado.
     * @throws EntidadNotFoundException si no existe un tipo de tramo con el ID dado.
     */
    @SuppressWarnings("null")
    public TipoTramo findById(Long id) {
        return tipoTramoRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("TipoTramo", id));
    }

    /**
     * Guarda un tipo de tramo en la base de datos.
     *
     * @param camion TipoTramo a guardar.
     * @return TipoTramo guardado.
     */
    @SuppressWarnings("null")
    public TipoTramo save(TipoTramo camion) {
        return tipoTramoRepository.save(camion);
    }

    /**
     * Busca un tipo de tramo por su nombre.
     *
     * @param nombre Nombre del tipo de tramo a buscar.
     * @return TipoTramo encontrado.
     * @throws EntidadNotFoundException si no existe un tipo de tramo con el nombre dado.
     */
    public TipoTramo findByNombre(String nombre) {
        return tipoTramoRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException("TipoTramo", nombre));
    }
}
