package com.tpi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tpi.repository.CamionRepository;

import jakarta.persistence.EntityNotFoundException;

import com.tpi.dto.request.ActualizarCamionRequest;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Camion;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CamionService {
    
    private final CamionRepository camionRepository;
    
    public List<Camion> findAll() {
        return camionRepository.findAll();
    }
    
    @SuppressWarnings("null")
    public Camion findById(Long id) {
        return camionRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Camión", id));
    }
    
    @SuppressWarnings("null")
    public Camion save(Camion camion) {
        return camionRepository.save(camion);
    }

    public List<Camion> findByCapacidades(Double pesoRequerido, Double volumenRequerido) {
        return camionRepository.findByCapacidadesSuficientes(pesoRequerido, volumenRequerido);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!camionRepository.existsById(id)) {
            throw new EntityNotFoundException("Camión no encontrado con ID: " + id);
        }
        camionRepository.deleteById(id);
    }

    @SuppressWarnings("null")
    public Camion actualizarCamion(Long id, ActualizarCamionRequest request) {
        Camion camionExistente = findById(id);
        
        // Actualizar solo los campos que no son null en el request
        if (request.dominio() != null) {
            camionExistente.setDominio(request.dominio());
        }
        if (request.nombreConductor() != null) {
            camionExistente.setNombreConductor(request.nombreConductor());
        }
        if (request.telefonoConductor() != null) {
            camionExistente.setTelefonoConductor(request.telefonoConductor());
        }
        if (request.disponible() != null) {
            camionExistente.setDisponible(request.disponible());
        }
        if (request.costoPorKm() != null) {
            camionExistente.setCostoPorKm(request.costoPorKm());
        }
        if (request.consumoCombustibleLx100km() != null) {
            camionExistente.setConsumoCombustibleLx100km(request.consumoCombustibleLx100km());
        }
        if (request.modelo() != null) {
            camionExistente.setModelo(request.modelo());
        }
        if (request.capacidadPesoKg() != null) {
            camionExistente.setCapacidadPesoKg(request.capacidadPesoKg());
        }
        if (request.capacidadVolumenM3() != null) {
            camionExistente.setCapacidadVolumenM3(request.capacidadVolumenM3());
        }
        
        return camionRepository.save(camionExistente);
    }
}
