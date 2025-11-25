package com.tpi.service;


import com.tpi.dto.request.ActualizarTarifaRequest;
import com.tpi.dto.request.TarifaRequest;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Tarifa;
import com.tpi.repository.TarifaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    /**
     * Obtiene una tarifa por su ID.
     *
     * @param id ID de la tarifa.
     * @return Tarifa encontrada.
     * @throws EntidadNotFoundException si no existe una tarifa con ese ID.
     */
    @SuppressWarnings("null")
    public Tarifa findById(Long id) {
        return tarifaRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("Tarifa", id));
    }

    /**
     * Busca una tarifa por su nombre exacto.
     *
     * @param nombre Nombre de la tarifa.
     * @return Tarifa encontrada.
     * @throws EntidadNotFoundException si no existe una tarifa con ese nombre.
     */
    public Tarifa findByNombre(String nombre) {
        return tarifaRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException("Tarifa", nombre));
    }

    /**
     * Devuelve todas las tarifas existentes.
     *
     * @return Lista de tarifas.
     */
    public List<Tarifa> findAll() {
        return tarifaRepository.findAll();
    }

    /**
     * Guarda o actualiza una tarifa en la base de datos.
     *
     * @param tarifa Tarifa a guardar o actualizar.
     * @return Tarifa guardada o actualizada.
     */
    @SuppressWarnings("null")
    public Tarifa save(Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }

    /**
     * Elimina una tarifa por su ID.
     *
     * @param id ID de la tarifa a eliminar.
     * @throws EntityNotFoundException si no existe una tarifa con ese ID.
     */
    @SuppressWarnings("null")
    public void deleteById(Long id) {
        if (!tarifaRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarifa no encontrada con ID: " + id);
        }
        tarifaRepository.deleteById(id);
    }


    /**
     * Actualiza los campos de una tarifa existente.
     *
     * @param id ID de la tarifa a actualizar.
     * @param request Objeto con los campos a modificar (solo se actualizan los no nulos).
     * @return La tarifa actualizada.
     * @throws EntidadNotFoundException si no existe una tarifa con el ID proporcionado.
     */
    @SuppressWarnings("null")
    public Tarifa actualizarTarifa(Long id, ActualizarTarifaRequest request) {

        Tarifa tarifaExistente = this.findById(id);
        
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
        if (request.precioCombustiblePorLitro() != null) {
            tarifaExistente.setPrecioCombustiblePorLitro(request.precioCombustiblePorLitro());
        }
        
        return tarifaRepository.save(tarifaExistente);
    }


    /**
     * Busca la tarifa correspondiente según el volumen del contenedor.
     * 
     * @param volumen Volumen del contenedor a evaluar (en m³)
     * @return Tarifa que cubre el rango de volumen dado
     * @throws EntityNotFoundException Si no se encuentra ninguna tarifa que incluya el volumen
     */
    public Tarifa getTarifaByVolumenContenedor(Double volumen) {
        // Obtener todas las tarifas de la base de datos
        List<Tarifa> tarifas = tarifaRepository.findAll();

        // Filtrar para encontrar la tarifa cuyo rango cubra el volumen
        return tarifas.stream()
                .filter(t -> volumen >= t.getVolumenMin() && volumen <= t.getVolumenMax())
                .findFirst() // Tomar la primera que cumpla la condición
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró tarifa para volumen: " + volumen
                ));
    }


    /**
     * Crea una nueva tarifa a partir del payload recibido.
     *
     * @param request Datos necesarios para la creación de la tarifa.
     * @return La entidad Tarifa creada y persistida en la base de datos.
     */
    @SuppressWarnings("null")
    public Tarifa crearTarifa(TarifaRequest request) {
        Tarifa tarifa = Tarifa.builder()
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .volumenMin(request.getVolumenMin())
            .volumenMax(request.getVolumenMax())
            .costoGestionPorTramo(request.getCostoGestionPorTramo())
            .precioCombustiblePorLitro(request.getPrecioCombustiblePorLitro())
            .build();

        return tarifaRepository.save(tarifa);
    }
}