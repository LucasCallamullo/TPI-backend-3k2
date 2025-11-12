package com.tpi.service;

import com.tpi.model.Contenedor;
import com.tpi.model.EstadoContenedor;
import com.tpi.repository.ContenedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;
    private final EstadoContenedorService estadoContenedorService;

    public List<Contenedor> findAll() {
        return contenedorRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Contenedor> findById(Long id) {
        return contenedorRepository.findById(id);
    }

    public Optional<Contenedor> findByIdentificacionUnica(String identificacionUnica) {
        return contenedorRepository.findByIdentificacionUnica(identificacionUnica);
    }

    @SuppressWarnings("null")
    public Contenedor save(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        contenedorRepository.deleteById(id);
    }

    public boolean existsByIdentificacionUnica(String identificacionUnica) {
        return contenedorRepository.existsByIdentificacionUnica(identificacionUnica);
    }

    /**
     * Crea un nuevo contenedor con estado por defecto "DISPONIBLE"
     */
    @SuppressWarnings("null")
    public Contenedor crearContenedor(Double peso, Double volumen, String identificacionUnica) {
        EstadoContenedor estadoDisponible = estadoContenedorService.findByNombre("Disponible")
            .orElseThrow(() -> new RuntimeException("Estado Disponible no encontrado"));

        Contenedor contenedor = Contenedor.builder()
            .peso(peso)
            .volumen(volumen)
            .identificacionUnica(identificacionUnica)
            .estado(estadoDisponible)
            .build();

        return contenedorRepository.save(contenedor);
    }

    /**
     * Cambia el estado de un contenedor
     */
    public Contenedor cambiarEstado(Long contenedorId, String nuevoEstadoNombre) {
        @SuppressWarnings("null")
        Contenedor contenedor = contenedorRepository.findById(contenedorId)
            .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));

        EstadoContenedor nuevoEstado = estadoContenedorService.findByNombre(nuevoEstadoNombre)
            .orElseThrow(() -> new RuntimeException("Estado " + nuevoEstadoNombre + " no encontrado"));

        contenedor.setEstado(nuevoEstado);
        return contenedorRepository.save(contenedor);
    }
}