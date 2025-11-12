package com.tpi.service;

import com.tpi.model.EstadoSolicitud;
import com.tpi.repository.EstadoSolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstadoSolicitudService {

    private final EstadoSolicitudRepository estadoSolicitudRepository;

    public List<EstadoSolicitud> findAll() {
        return estadoSolicitudRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<EstadoSolicitud> findById(Long id) {
        return estadoSolicitudRepository.findById(id);
    }

    public Optional<EstadoSolicitud> findByNombre(String nombre) {
        return estadoSolicitudRepository.findByNombre(nombre);
    }

    @SuppressWarnings("null")
    public EstadoSolicitud save(EstadoSolicitud estadoSolicitud) {
        return estadoSolicitudRepository.save(estadoSolicitud);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        estadoSolicitudRepository.deleteById(id);
    }

    public boolean existsByNombre(String nombre) {
        return estadoSolicitudRepository.existsByNombre(nombre);
    }
}