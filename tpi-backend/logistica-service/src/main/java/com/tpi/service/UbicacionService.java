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

    /**
     * Crea una nueva ubicación a partir de los datos del request.
     * Primero valida que el tipo de ubicación exista.
     *
     * @param request DTO con los datos de la nueva ubicación.
     * @return la ubicación creada y persistida.
     */
    @SuppressWarnings("null")
    public Ubicacion crearUbicacion(UbicacionRequestDTO request) {
        TipoUbicacion tipo = tipoUbicacionService.findById(request.tipoId());

        Ubicacion ubicacion = Ubicacion.builder()
            .direccion(request.direccion())
            .nombre(request.nombre())
            .latitud(request.latitud())
            .longitud(request.longitud())
            .tipo(tipo)
            .build();
            
        return ubicacionRepository.save(ubicacion);
    }

    /**
     * Busca una ubicación por ID.
     * Si no existe, lanza EntidadNotFoundException.
     *
     * @param id identificador de la ubicación.
     * @return la ubicación encontrada.
     */
    @SuppressWarnings("null")
    public Ubicacion findById(Long id) {
        return ubicacionRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("Ubicacion", id));
    }

    /**
     * Obtiene todas las ubicaciones convertidas a DTO.
     *
     * @return lista de UbicacionResponseDTO.
     */
    public List<UbicacionResponseDTO> obtenerTodas() {
        return ubicacionRepository.findAll().stream()
            .map(UbicacionResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Actualiza todos los campos de una ubicación existente.
     *
     * @param id ID de la ubicación a actualizar.
     * @param request DTO con los nuevos valores.
     * @return la ubicación actualizada.
     */
    public Ubicacion actualizarUbicacion(Long id, UbicacionRequestDTO request) {
        Ubicacion existente = findById(id);

        existente.setDireccion(request.direccion());
        existente.setNombre(request.nombre());
        existente.setLatitud(request.latitud());
        existente.setLongitud(request.longitud());
        TipoUbicacion tipo = tipoUbicacionService.findById(request.tipoId());
        existente.setTipo(tipo);

        return ubicacionRepository.save(existente);
    }

    /**
     * Actualiza parcialmente una ubicación.
     * Solo modifica los campos que estén presentes en el Map.
     *
     * @param id ID de la ubicación a actualizar.
     * @param updates map con los campos y valores a modificar.
     * @return la ubicación actualizada parcialmente.
     */
    @SuppressWarnings("null")
    public Ubicacion actualizarParcialUbicacion(Long id, Map<String, Object> updates) {
        Ubicacion existente = findById(id);

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
                    // Campo desconocido → ignorado
                    break;
            }
        });

        return ubicacionRepository.save(existente);
    }

    /**
     * Elimina una ubicación por su ID.
     * Si no existe, lanza EntityNotFoundException.
     *
     * @param id identificador de la ubicación a eliminar.
     */
    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!ubicacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Ubicación no encontrada con ID: " + id);
        }
        ubicacionRepository.deleteById(id);
    }

}