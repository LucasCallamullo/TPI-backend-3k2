
package com.tpi.controller;

import com.tpi.model.TipoUbicacion;
import com.tpi.repository.TipoUbicacionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-ubicacion")
public class TipoUbicacionController {

    private final TipoUbicacionRepository tipoUbicacionRepository;

    public TipoUbicacionController(TipoUbicacionRepository tipoUbicacionRepository) {
        this.tipoUbicacionRepository = tipoUbicacionRepository;
    }

    @GetMapping
    public List<TipoUbicacion> listarTiposUbicacion() {
        return tipoUbicacionRepository.findAll();
    }

    @PostMapping
    public TipoUbicacion crearTipoUbicacion(@RequestBody TipoUbicacion tipoUbicacion) {
        return tipoUbicacionRepository.save(tipoUbicacion);
    }

    @GetMapping("/{id}")
    public TipoUbicacion obtenerPorId(@PathVariable Long id) {
        return tipoUbicacionRepository.findById(id).orElse(null);
    }
}