package com.tpi.controller;

import com.tpi.model.TipoTramo;
import com.tpi.repository.TipoTramoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipo-tramos")
public class TipoTramoController {

    private final TipoTramoRepository tipoTramoRepository;

    public TipoTramoController(TipoTramoRepository tipoTramoRepository) {
        this.tipoTramoRepository = tipoTramoRepository;
    }

    @GetMapping
    public List<TipoTramo> listarTipoTramos() {
        return tipoTramoRepository.findAll();
    }

    @PostMapping
    public TipoTramo crearTipoTramo(@RequestBody TipoTramo tipoTramo) {
        return tipoTramoRepository.save(tipoTramo);
    }

    @GetMapping("/{id}")
    public TipoTramo obtenerPorId(@PathVariable Integer id) {
        return tipoTramoRepository.findById(id).orElse(null);
    }
}
