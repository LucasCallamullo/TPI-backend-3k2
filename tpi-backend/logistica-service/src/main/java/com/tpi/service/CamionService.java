package com.tpi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tpi.repository.CamionRepository;

import jakarta.persistence.EntityNotFoundException;

import com.tpi.dto.request.ActualizarCamionRequest;
import com.tpi.dto.request.CamionRequest;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Camion;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CamionService {
    
    private final CamionRepository camionRepository;
    
    /**
     * Obtiene todos los camiones registrados.
     * @return Lista completa de entidades Camion.
     */
    public List<Camion> findAll() {
        return camionRepository.findAll();
    }

    /**
     * Busca un camión por ID.
     * Lanza excepción si no existe.
     * @param id ID del camión a buscar.
     * @return Entidad Camion encontrada.
     */
    @SuppressWarnings("null")
    public Camion findById(Long id) {
        return camionRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Camión", id));
    }

    /**
     * Guarda o actualiza un camión.
     * @param camion Entidad a guardar.
     * @return Camion guardado.
     */
    @SuppressWarnings("null")
    public Camion save(Camion camion) {
        return camionRepository.save(camion);
    }

    /**
     * Busca camiones que cumplan con capacidades mínimas de peso y volumen.
     * @param pesoRequerido Peso requerido.
     * @param volumenRequerido Volumen requerido.
     * @return Lista de camiones que cumplen con los requisitos.
     */
    public List<Camion> findByCapacidades(Double pesoRequerido, Double volumenRequerido) {
        return camionRepository.findByCapacidadesSuficientes(pesoRequerido, volumenRequerido);
    }

    /**
     * Elimina un camión por ID.
     * Lanza excepción si no existe.
     * @param id ID del camión a eliminar.
     */
    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!camionRepository.existsById(id)) {
            throw new EntityNotFoundException("Camión no encontrado con ID: " + id);
        }
        camionRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente un camión.
     * Solo modifica los valores enviados en el request.
     * @param id ID del camión a actualizar.
     * @param request Datos nuevos.
     * @return Camion actualizado.
     */
    @SuppressWarnings("null")
    public Camion actualizarCamion(Long id, ActualizarCamionRequest request) {
        Camion camionExistente = findById(id);
        
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


    /**
     * Crea un nuevo camión en el sistema a partir de los datos proporcionados.
     *
     * @param request Objeto {@link CamionRequest} que contiene los datos necesarios
     *                para registrar un camión, incluyendo dominio, conductor,
     *                capacidades y costos operativos.
     *
     * @return El camión creado y persistido en la base de datos.
     *
     * Notas:
     * - Si no se especifica el campo "disponible", se establece por defecto en true.
     * - El dominio debe ser único y no nulo.
     */
    @SuppressWarnings("null")
    public Camion crearCamion(CamionRequest request) {

        Camion camion = Camion.builder()
            .dominio(request.dominio())
            .nombreConductor(request.nombreConductor())
            .telefonoConductor(request.telefonoConductor())
            .disponible(request.disponible() != null ? request.disponible() : true)
            .costoPorKm(request.costoPorKm())
            .consumoCombustibleLx100km(request.consumoCombustibleLx100km())
            .modelo(request.modelo())
            .capacidadPesoKg(request.capacidadPesoKg())
            .capacidadVolumenM3(request.capacidadVolumenM3())
            .build();

        return camionRepository.save(camion);
    }
}
