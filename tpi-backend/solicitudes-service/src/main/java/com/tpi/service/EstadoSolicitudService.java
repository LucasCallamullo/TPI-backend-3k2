package com.tpi.service;

import com.tpi.model.EstadoSolicitud;
import com.tpi.repository.EstadoSolicitudRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public EstadoSolicitud findByNombre(String nombre) {
        return estadoSolicitudRepository.findByNombre(nombre)
                                    .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Estado inválido: " + nombre
                                    ));
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