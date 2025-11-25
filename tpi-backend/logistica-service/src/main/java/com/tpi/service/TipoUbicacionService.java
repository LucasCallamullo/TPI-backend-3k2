package com.tpi.service;

import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.TipoUbicacion;
import com.tpi.repository.TipoUbicacionRepository;

import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipoUbicacionService {

    private final TipoUbicacionRepository tipoUbicacionRepository;

    /**
     * Busca un tipo de ubicación por su ID.
     *
     * @param id ID del tipo de ubicación.
     * @return TipoUbicacion encontrado.
     * @throws EntidadNotFoundException si no existe un tipo de ubicación con el ID dado.
     */
    @SuppressWarnings("null")
    public TipoUbicacion findById(Long id) {
        return tipoUbicacionRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("TipoUbicacion", id));
    }

    /**
     * Busca un tipo de ubicación por su nombre.
     *
     * @param nombre Nombre del tipo de ubicación.
     * @return Optional con el TipoUbicacion si existe, vacío si no.
     */
    public TipoUbicacion findByNombre(String nombre) {
        return tipoUbicacionRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException("TipoUbicacion", nombre));
    }

    /**
     * Guarda un tipo de ubicación en la base de datos.
     *
     * @param e TipoUbicacion a guardar.
     * @return TipoUbicacion guardado.
     */
    @SuppressWarnings("null")
    public TipoUbicacion save(TipoUbicacion e) {
        return tipoUbicacionRepository.save(e);
    }

    /**
     * Devuelve todos los tipos de ubicación existentes.
     *
     * @return Lista de todos los tipos de ubicación.
     */
    public List<TipoUbicacion> findAll() {
        return tipoUbicacionRepository.findAll();
    }

    /**
     * Actualiza completamente un tipo de ubicación existente.
     *
     * @param id ID del tipo de ubicación a actualizar.
     * @param tipoUbicacion Datos nuevos para actualizar.
     * @return TipoUbicacion actualizado.
     */
    public TipoUbicacion update(Long id, TipoUbicacion tipoUbicacion) {
        TipoUbicacion existente = findById(id);

        existente.setNombre(tipoUbicacion.getNombre());
        existente.setDescripcion(tipoUbicacion.getDescripcion());
        // Agregar otros campos si los hay

        return tipoUbicacionRepository.save(existente);
    }

    /**
     * Actualiza parcialmente un tipo de ubicación existente.
     *
     * @param id ID del tipo de ubicación a actualizar.
     * @param updates Mapa con los campos y valores a actualizar.
     * @return TipoUbicacion actualizado.
     */
    @SuppressWarnings("null")
    public TipoUbicacion actualizarParcial(Long id, Map<String, Object> updates) {

        TipoUbicacion existente = this.findById(id);

        updates.forEach((campo, valor) -> {
            switch (campo) {
                case "nombre" -> existente.setNombre((String) valor);
                case "descripcion" -> existente.setDescripcion((String) valor);
                // Ignorar campos desconocidos
            }
        });

        return tipoUbicacionRepository.save(existente);
    }

    /**
     * Elimina un tipo de ubicación por su ID.
     *
     * @param id ID del tipo de ubicación a eliminar.
     * @throws EntityNotFoundException si no existe un tipo de ubicación con ese ID.
     */
    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!tipoUbicacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Tipo de ubicación no encontrado con ID: " + id);
        }
        tipoUbicacionRepository.deleteById(id);
    }

}