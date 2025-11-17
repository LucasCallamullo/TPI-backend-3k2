package com.tpi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tpi.dto.request.UbicacionRequestDTO;
import com.tpi.dto.response.UbicacionDTOs.UbicacionResponseDTO;

import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.TipoUbicacion;
import com.tpi.model.Ubicacion;
import com.tpi.repository.UbicacionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final TipoUbicacionService tipoUbicacionService;

    @SuppressWarnings("null")
    public Ubicacion crearUbicacion(UbicacionRequestDTO request) {
        // 1. Validar que el tipo existe
        TipoUbicacion tipo = tipoUbicacionService.findById(request.tipoId());
        
        // 2. Crear ubicación
        Ubicacion ubicacion = Ubicacion.builder()
            .direccion(request.direccion())
            .nombre(request.nombre())
            .latitud(request.latitud())
            .longitud(request.longitud())
            .tipo(tipo)
            .build();
            
        return ubicacionRepository.save(ubicacion);
    }

    // Método findById para Ubicacion
    @SuppressWarnings("null")
    public Ubicacion findById(Long id) {
        return ubicacionRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("Ubicacion", id));
    }

    public List<UbicacionResponseDTO> obtenerTodas() {
        return ubicacionRepository.findAll().stream()
            .map(UbicacionResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public Ubicacion actualizarUbicacion(Long id, UbicacionRequestDTO request) {
        Ubicacion existente = findById(id);
        
        // Actualizar todos los campos
        existente.setDireccion(request.direccion());
        existente.setNombre(request.nombre());
        existente.setLatitud(request.latitud());
        existente.setLongitud(request.longitud());
        TipoUbicacion tipo = tipoUbicacionService.findById(request.tipoId());
        existente.setTipo(tipo);
        return ubicacionRepository.save(existente);
    }

    @SuppressWarnings("null")
    public Ubicacion actualizarParcialUbicacion(Long id, Map<String, Object> updates) {
        Ubicacion existente = findById(id);
        
        // Actualizar solo los campos presentes en el map
        updates.forEach((campo, valor) -> {
            switch (campo) {
                case "direccion":
                    existente.setDireccion((String) valor);
                    break;
                case "nombre":
                    existente.setNombre((String) valor);
                    break;
                case "latitud":
                    existente.setLatitud((Double) valor);
                    break;
                case "longitud":
                    existente.setLongitud((Double) valor);
                    break;
                case "tipoUbicacionId":
                    Long tipoId = ((Number) valor).longValue();
                    TipoUbicacion tipo = tipoUbicacionService.findById(tipoId);
                    existente.setTipo(tipo);
                    break;
                default:
                    // Ignorar campos desconocidos
                    break;
            }
        });
        
        return ubicacionRepository.save(existente);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!ubicacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Ubicación no encontrada con ID: " + id);
        }
        ubicacionRepository.deleteById(id);
    }
}