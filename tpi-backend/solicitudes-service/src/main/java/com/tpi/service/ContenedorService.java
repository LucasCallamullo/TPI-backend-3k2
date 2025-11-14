package com.tpi.service;

import com.tpi.dto.request.ContenedorRequestDTO;
import com.tpi.dto.response.ContenedorResponseDTO;
import com.tpi.model.Contenedor;
import com.tpi.model.EstadoContenedor;
import com.tpi.repository.ContenedorRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;
    private final EstadoContenedorService estadoContenedorService;

    /**
     * Obtener todos los contenedores, opcionalmente filtrados por estado
     */
    public List<ContenedorResponseDTO> findAll(String estado) {
        List<Contenedor> contenedores;
        
        if (estado != null && !estado.trim().isEmpty()) {
            // 1 SOLA query con JOIN
            contenedores = contenedorRepository.findByEstadoNombreWithEstado(estado);
        } else {
            // 1 SOLA query con JOIN
            contenedores = contenedorRepository.findAllWithEstado();
        }
        
        return contenedores.stream()
                .map(ContenedorResponseDTO::fromEntity) // solucion a getEstado() NO hace query extra
                .collect(Collectors.toList());
    }

    /**
     * Obtener contenedor por ID
     */
    public ContenedorResponseDTO findById(Long id) {
        Contenedor contenedor = contenedorRepository.findByIdWithEstado(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "Contenedor no encontrado con ID: " + id
                ));
        return ContenedorResponseDTO.fromEntity(contenedor);
    }

    /**
     * Actualizar estado de un contenedor
     */
    @SuppressWarnings("null")
    public ContenedorResponseDTO actualizarEstado(Long id, String nombreEstado) {
        Contenedor contenedor = contenedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "Contenedor no encontrado con ID: " + id
                ));
        
        EstadoContenedor estado = estadoContenedorService.findByNombre(nombreEstado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estado de contenedor inválido: " + nombreEstado
                ));
        
        contenedor.setEstado(estado);
        Contenedor updated = contenedorRepository.save(contenedor);
        
        return ContenedorResponseDTO.fromEntity(updated);
    }

    public Optional<Contenedor> findByIdentificacionUnica(String identificacionUnica) {
        return contenedorRepository.findByIdentificacionUnica(identificacionUnica);
    }

    public boolean existsByIdentificacionUnica(String identificacionUnica) {
        return contenedorRepository.existsByIdentificacionUnica(identificacionUnica);
    }

    @SuppressWarnings("null")
    public Contenedor crearContenedor(ContenedorRequestDTO request) {
        // Validar que la identificación única no exista
        if (contenedorRepository.existsByIdentificacionUnica(request.identificacionUnica())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Ya existe un contenedor con la identificación: " + request.identificacionUnica()
            );
        }
        
        EstadoContenedor estadoDisponible = estadoContenedorService.findByNombre("DISPONIBLE")
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Estado 'DISPONIBLE' no configurado en el sistema"
            ));
        
        Contenedor contenedor = Contenedor.builder()
            .peso(request.peso())
            .volumen(request.volumen())
            .identificacionUnica(request.identificacionUnica())
            .estado(estadoDisponible)
            .build();
            
        return contenedorRepository.save(contenedor);
    }

    @SuppressWarnings("null")
    public Contenedor save(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        contenedorRepository.deleteById(id);
    }
}