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

    public List<EstadoContenedor> findAll() {
        return estadoContenedorRepository.findAll();
    }

    @SuppressWarnings("null")
    public EstadoContenedor findById(Long id) {
        return estadoContenedorRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException(
                "EstadoSolicitud", 
                "No se encontró el estado con nombre: " + id
            ));
    }

    public EstadoContenedor findByNombre(String nombre) {
        return estadoContenedorRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException(
                "EstadoSolicitud", 
                "No se encontró el estado con nombre: " + nombre
            ));
    }

    @SuppressWarnings("null")
    public EstadoContenedor save(EstadoContenedor estadoContenedor) {
        return estadoContenedorRepository.save(estadoContenedor);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        estadoContenedorRepository.deleteById(id);
    }

    public boolean existsByNombre(String nombre) {
        return estadoContenedorRepository.existsByNombre(nombre);
    }
}