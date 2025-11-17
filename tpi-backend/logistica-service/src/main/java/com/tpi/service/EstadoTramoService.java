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
    
    public List<EstadoTramo> findAll() {
        return estadoTramoRepository.findAll();
    }
    
    @SuppressWarnings("null")
    public EstadoTramo findById(Long id) {
        return estadoTramoRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("EstadoTramo", id));
    }
    
    @SuppressWarnings("null")
    public EstadoTramo save(EstadoTramo camion) {
        return estadoTramoRepository.save(camion);
    }
    
    public EstadoTramo findByNombre(String nombre) {
        return estadoTramoRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException("EstadoTramo", nombre));
    }
}