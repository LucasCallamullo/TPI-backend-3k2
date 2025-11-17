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

    public List<EstadoSolicitud> findAll() {
        return estadoSolicitudRepository.findAll();
    }

    @SuppressWarnings("null")
    public EstadoSolicitud findById(Long id) {
        return estadoSolicitudRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException(
                "EstadoSolicitud", 
                "No se encontró el estado con nombre: " + id
            ));
    }

    public EstadoSolicitud findByNombre(String nombre) {
        return estadoSolicitudRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException(
                "EstadoSolicitud", 
                "No se encontró el estado con nombre: " + nombre
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