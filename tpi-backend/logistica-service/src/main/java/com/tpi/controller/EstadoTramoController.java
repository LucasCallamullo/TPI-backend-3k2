package com.tpi.controller;

import com.tpi.model.EstadoTramo;
import com.tpi.service.EstadoTramoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estado-tramos")
public class EstadoTramoController {

    private final EstadoTramoService estadoTramoService;

    public EstadoTramoController(EstadoTramoService estadoTramoService) {
        this.estadoTramoService = estadoTramoService;
    }

    @GetMapping
    public List<EstadoTramo> listarEstadoTramos() {
        return estadoTramoService.findAll();
    }

    @SuppressWarnings("null")
    @PostMapping
    public EstadoTramo crearEstadoTramo(@RequestBody EstadoTramo estadoTramo) {
        return estadoTramoService.save(estadoTramo);
    }

    @SuppressWarnings("null")
    @GetMapping("/{id}")
    public EstadoTramo obtenerPorId(@PathVariable Long id) {
        return estadoTramoService.findById(id);
    }
}


