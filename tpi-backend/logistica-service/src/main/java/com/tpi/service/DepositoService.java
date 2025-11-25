package com.tpi.service;

import com.tpi.repository.DepositoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;

import com.tpi.dto.request.ActualizarDepositoRequest;
import com.tpi.dto.request.DepositoRequest;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Deposito;
import com.tpi.model.Ubicacion;

@Service
@RequiredArgsConstructor
public class DepositoService {
    
    private final DepositoRepository depositoRepository;
    private final UbicacionService ubicacionService;
    
    /**
     * Obtiene todos los depósitos registrados.
     * @return Lista completa de entidades Deposito.
     */
    public List<Deposito> findAll() {
        return depositoRepository.findAll();
    }

    /**
     * Busca múltiples depósitos por una lista de IDs.
     * @param listaIds Lista de identificadores de depósitos.
     * @return Lista de depósitos encontrados (solo los que existen).
     */
    @SuppressWarnings("null")
    public List<Deposito> findAllById(List<Long> listaIds) {
        return depositoRepository.findAllById(listaIds);
    }

    /**
     * Obtiene un depósito por su ID.
     * Lanza una excepción si no existe.
     * @param id ID del depósito.
     * @return Entidad Deposito encontrada.
     */
    @SuppressWarnings("null")
    public Deposito findById(Long id) {
        return depositoRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("Depósito", id));
    }

    /**
     * Guarda o actualiza un depósito.
     * @param deposito Entidad Depósito a guardar.
     * @return Deposito guardado.
     */
    @SuppressWarnings("null")
    public Deposito save(Deposito deposito) {
        return depositoRepository.save(deposito);
    }

    /**
     * Actualiza parcialmente un depósito existente.
     * Solo modifica los campos enviados en el request.
     * @param id ID del depósito a actualizar.
     * @param request Datos nuevos para actualizar.
     * @return Deposito actualizado.
     */
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

    /**
     * Elimina un depósito por ID.
     * Lanza excepción si el depósito no existe.
     * @param id ID del depósito a eliminar.
     */
    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!depositoRepository.existsById(id)) {
            throw new EntityNotFoundException("Depósito no encontrado con ID: " + id);
        }
        depositoRepository.deleteById(id);
    }


    /**
     * Crea un nuevo depósito junto con su ubicación asociada.
     *
     * El método delega la creación de la ubicación al {@code ubicacionService},
     * y luego construye la entidad {@code Deposito} para persistirla en la base de datos.
     *
     * @param request DTO con los datos necesarios para crear el depósito y su ubicación.
     * @return El depósito creado y persistido.
     */
    @SuppressWarnings("null")
    public Deposito crearDeposito(DepositoRequest request) {

        Ubicacion ubicacion = ubicacionService.crearUbicacion(request.ubicacion());

        Deposito deposito = Deposito.builder()
                .nombre(request.nombre())
                .costoEstadiaPorDia(request.costoEstadiaPorDia())
                .ubicacion(ubicacion)
                .build();

        return depositoRepository.save(deposito);
    }
}