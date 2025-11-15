package com.tpi.service;

import com.tpi.dto.request.ContenedorRequestDTO;
import com.tpi.dto.response.ContenedorResponseDTO;
import com.tpi.dto.response.EstadoContenedorInfoDTO;
import com.tpi.exception.EntidadDuplicadaException;
import com.tpi.exception.EntidadNotFoundException;
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
        
        // Crea el DTO del estado explícitamente
        EstadoContenedorInfoDTO estadoDTO = new EstadoContenedorInfoDTO(
            estado.getId(),
            estado.getNombre()
        );
        
        return ContenedorResponseDTO.fromEntity(updated, estadoDTO);
    }

    /**
     * Metodos para reutilizar en otros a partir de la misma clase 
     */
    public Optional<Contenedor> findByIdentificacionUnica(String identificacionUnica) {
        return contenedorRepository.findByIdentificacionUnica(identificacionUnica);
    }

    public boolean existsByIdentificacionUnica(String identificacionUnica) {
        return contenedorRepository.existsByIdentificacionUnica(identificacionUnica);
    }

    /**
     * Para crear contenedor y dar un response apropiado se llama desde POST especifico
     * @param requestDTO
     * @return
     */
    public ContenedorResponseDTO crearContenedor(ContenedorRequestDTO requestDTO) {
        // Validar que el estado exista
        EstadoContenedor estado = estadoContenedorService.findByNombre("DISPONIBLE")
            .orElseThrow(() -> new EntidadNotFoundException(
                "Estado Contenedor", 
                "Estado DISPONIBLE no configurado en el sistema"
            ));

        // Validar duplicado usando excepción genérica
        if (existsByIdentificacionUnica(requestDTO.identificacionUnica())) {
            throw new EntidadDuplicadaException(
                "Contenedor", 
                "identificación única", 
                requestDTO.identificacionUnica()
            );
        }
        
        Contenedor contenedor = new Contenedor();
        contenedor.setPeso(requestDTO.peso());
        contenedor.setVolumen(requestDTO.volumen());
        contenedor.setIdentificacionUnica(requestDTO.identificacionUnica());
        contenedor.setEstado(estado);
        
        Contenedor contenedorGuardado = contenedorRepository.save(contenedor);
        
        EstadoContenedorInfoDTO estadoDTO = new EstadoContenedorInfoDTO(
            estado.getId(),
            estado.getNombre()
        );
        
        return ContenedorResponseDTO.fromEntity(contenedorGuardado, estadoDTO);
    }

    /**
     * Para crear una entidad y devolverla se llama desde POST de solicitudes que requiere crear un contenedor
     * 
     * @param requestDTO
     * @return
     */
    public Contenedor crearContenedorEntidad(ContenedorRequestDTO requestDTO) {
        // Validar que el estado exista
        EstadoContenedor estado = estadoContenedorService.findByNombre("DISPONIBLE")
            .orElseThrow(() -> new EntidadNotFoundException(
                "Estado Contenedor", 
                "Estado DISPONIBLE no configurado en el sistema"
            ));

        // Validar duplicado usando excepción genérica
        if (existsByIdentificacionUnica(requestDTO.identificacionUnica())) {
            throw new EntidadDuplicadaException(
                "Contenedor", 
                "identificación única", 
                requestDTO.identificacionUnica()
            );
        }
        
        Contenedor contenedor = new Contenedor();
        contenedor.setPeso(requestDTO.peso());
        contenedor.setVolumen(requestDTO.volumen());
        contenedor.setIdentificacionUnica(requestDTO.identificacionUnica());
        contenedor.setEstado(estado);
        contenedorRepository.save(contenedor);
        return contenedor;
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