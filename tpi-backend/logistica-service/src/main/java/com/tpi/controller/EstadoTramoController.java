package com.tpi.controller;

import com.tpi.model.EstadoTramo;
import com.tpi.repository.EstadoTramoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estado-tramos")
public class EstadoTramoController {

    private final EstadoTramoRepository estadoTramoRepository;

    public EstadoTramoController(EstadoTramoRepository estadoTramoRepository) {
        this.estadoTramoRepository = estadoTramoRepository;
    }

    @GetMapping
    public List<EstadoTramo> listarEstadoTramos() {
        return estadoTramoRepository.findAll();
    }

    @PostMapping
    public EstadoTramo crearEstadoTramo(@RequestBody EstadoTramo estadoTramo) {
        return estadoTramoRepository.save(estadoTramo);
    }

    @GetMapping("/{id}")
    public EstadoTramo obtenerPorId(@PathVariable Integer id) {
        return estadoTramoRepository.findById(id).orElse(null);
    }
}


