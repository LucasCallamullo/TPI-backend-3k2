package com.tpi.service;

import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.TipoUbicacion;
import com.tpi.repository.TipoUbicacionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoUbicacionService {

    private final TipoUbicacionRepository tipoUbicacionRepository;

    @SuppressWarnings("null")
    public TipoUbicacion findById(Long id) {
        return tipoUbicacionRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("TipoUbicacion", id));
    }

    public Optional<TipoUbicacion> findByNombre(String nombre) {
        return tipoUbicacionRepository.findByNombre(nombre);
    }

    @SuppressWarnings("null")
    public TipoUbicacion save(TipoUbicacion e) {
        return tipoUbicacionRepository.save(e);
    }

    public List<TipoUbicacion> findAll() {
        return tipoUbicacionRepository.findAll();
    }

    public TipoUbicacion update(Long id, TipoUbicacion tipoUbicacion) {
        TipoUbicacion existente = findById(id);
        
        // Actualizar todos los campos
        existente.setNombre(tipoUbicacion.getNombre());
        existente.setDescripcion(tipoUbicacion.getDescripcion());
        // Agrega aquí otros campos que tenga tu entidad TipoUbicacion
        
        return tipoUbicacionRepository.save(existente);
    }

    @SuppressWarnings("null")
    public TipoUbicacion actualizarParcial(Long id, Map<String, Object> updates) {
        TipoUbicacion existente = findById(id);
        
        // Actualizar solo los campos presentes en el map
        updates.forEach((campo, valor) -> {
            switch (campo) {
                case "nombre":
                    existente.setNombre((String) valor);
                    break;
                case "descripcion":
                    existente.setDescripcion((String) valor);
                    break;
                    
                default:
                    // Ignorar campos desconocidos o lanzar excepción
                    break;
            }
        });
        
        return tipoUbicacionRepository.save(existente);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!tipoUbicacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Tipo de ubicación no encontrado con ID: " + id);
        }
        tipoUbicacionRepository.deleteById(id);
    }
}