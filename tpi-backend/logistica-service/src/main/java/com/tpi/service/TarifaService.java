package com.tpi.service;


import com.tpi.dto.request.ActualizarTarifaRequest;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Tarifa;
import com.tpi.repository.TarifaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    /**
     * Obtiene una tarifa por ID (lanza excepción si no existe)
     */
    @SuppressWarnings("null")
    public Tarifa findById(Long id) {
        return tarifaRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("Tarifa", id));
    }

    /**
     * Busca tarifa por nombre exacto
     */
    public Optional<Tarifa> findByNombre(String nombre) {
        return tarifaRepository.findByNombre(nombre);
    }

    /**
     * Obtiene todas las tarifas
     */
    public List<Tarifa> findAll() {
        return tarifaRepository.findAll();
    }

    /**
     * Guarda o actualiza una tarifa
     */
    @SuppressWarnings("null")
    public Tarifa save(Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }

    @SuppressWarnings("null")
    public Tarifa actualizarTarifa(Long id, ActualizarTarifaRequest request) {
        Tarifa tarifaExistente = findById(id);
        
        if (request.nombre() != null) {
            tarifaExistente.setNombre(request.nombre());
        }
        if (request.descripcion() != null) {
            tarifaExistente.setDescripcion(request.descripcion());
        }
        if (request.volumenMin() != null) {
            tarifaExistente.setVolumenMin(request.volumenMin());
        }
        if (request.volumenMax() != null) {
            tarifaExistente.setVolumenMax(request.volumenMax());
        }
        if (request.costoGestionPorTramo() != null) {
            tarifaExistente.setCostoGestionPorTramo(request.costoGestionPorTramo());
        }
        if (request.costoPorKmBase() != null) {
            tarifaExistente.setCostoPorKmBase(request.costoPorKmBase());
        }
        if (request.precioCombustiblePorLitro() != null) {
            tarifaExistente.setPrecioCombustiblePorLitro(request.precioCombustiblePorLitro());
        }
        
        return tarifaRepository.save(tarifaExistente);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!tarifaRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarifa no encontrada con ID: " + id);
        }
        tarifaRepository.deleteById(id);
    }
}