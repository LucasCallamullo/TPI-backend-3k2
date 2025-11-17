package com.tpi.service;

import com.tpi.repository.DepositoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;

import com.tpi.dto.request.ActualizarDepositoRequest;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Deposito;
import com.tpi.model.Ubicacion;

@Service
@RequiredArgsConstructor
public class DepositoService {
    
    private final DepositoRepository depositoRepository;
    private final UbicacionService ubicacionService;
    
    
    public List<Deposito> findAll() {
        return depositoRepository.findAll();
    }

    @SuppressWarnings("null")
    public List<Deposito> findAllById(List<Long> listaIds) {
        return depositoRepository.findAllById(listaIds);
    }
    
    @SuppressWarnings("null")
    public Deposito findById(Long id) {
        return depositoRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Depósito", id));
    }
    
    @SuppressWarnings("null")
    public Deposito save(Deposito deposito) {
        return depositoRepository.save(deposito);
    }

    @SuppressWarnings("null")
    public Deposito actualizarDeposito(Long id, ActualizarDepositoRequest request) {
        Deposito depositoExistente = findById(id);
        
        if (request.nombre() != null) {
            depositoExistente.setNombre(request.nombre());
        }
        if (request.costoEstadiaPorDia() != null) {
            depositoExistente.setCostoEstadiaPorDia(request.costoEstadiaPorDia());
        }
        if (request.ubicacionId() != null) {
            Ubicacion ubicacion = ubicacionService.findById(request.ubicacionId());
            depositoExistente.setUbicacion(ubicacion);
        }
        
        return depositoRepository.save(depositoExistente);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!depositoRepository.existsById(id)) {
            throw new EntityNotFoundException("Depósito no encontrado con ID: " + id);
        }
        depositoRepository.deleteById(id);
    }
}