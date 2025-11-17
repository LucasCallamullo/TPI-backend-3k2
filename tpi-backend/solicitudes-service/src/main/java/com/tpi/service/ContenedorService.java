package com.tpi.service;

import com.tpi.dto.request.ContenedorRequestDTO;
import com.tpi.dto.response.ContenedorResponseDTO;
import com.tpi.dto.response.EstadoContenedorInfoDTO;
import com.tpi.exception.ContenedorNoDisponibleException;
import com.tpi.exception.EntidadDuplicadaException;
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
        
        EstadoContenedor estado = estadoContenedorService.findByNombre(nombreEstado);
        
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
        EstadoContenedor estado = estadoContenedorService.findByNombre("DISPONIBLE");

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
     * Obtiene o crea un contenedor como entidad
     */
    public Contenedor getOrCreate(ContenedorRequestDTO requestDTO, String keycloakId) {
        EstadoContenedor estadoDisponible = estadoContenedorService.findByNombre("DISPONIBLE");

        // Buscar o crear contenedor
        Contenedor contenedor = findByIdentificacionUnica(requestDTO.identificacionUnica())
            .orElseGet(() -> crearContenedorDisponible(requestDTO, estadoDisponible));

        // Validar disponibilidad
        if (!contenedor.getEstado().getNombre().equals(estadoDisponible.getNombre())) {
            throw new ContenedorNoDisponibleException("El contenedor no está disponible");
        }

        // Asignar al cliente
        return asignarACliente(contenedor, keycloakId);
    }

    private Contenedor crearContenedorDisponible(ContenedorRequestDTO requestDTO, EstadoContenedor estado) {
        Contenedor contenedor = new Contenedor();
        contenedor.setIdentificacionUnica(requestDTO.identificacionUnica());
        contenedor.setPeso(requestDTO.peso());
        contenedor.setVolumen(requestDTO.volumen());
        contenedor.setEstado(estado);
        contenedor.setClienteId(null); // Sin dueño inicialmente
        
        return this.save(contenedor);
    }

    private Contenedor asignarACliente(Contenedor contenedor, String keycloakId) {
        if (contenedor.getClienteId() == null || !contenedor.getClienteId().equals(keycloakId)) {
            contenedor.setClienteId(keycloakId);
            return this.save(contenedor);
        }
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