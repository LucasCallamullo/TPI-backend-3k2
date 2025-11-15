package com.tpi.controller;

import com.tpi.model.Tramo;
import com.tpi.repository.TramoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tramos")
public class TramoController {

    private final TramoRepository tramoRepository;

    public TramoController(TramoRepository tramoRepository) {
        this.tramoRepository = tramoRepository;
    }

    @GetMapping
    public List<Tramo> listarTramos() {
        return tramoRepository.findAll();
    }

    @PostMapping
    public Tramo crearTramo(@RequestBody Tramo tramo) {
        return tramoRepository.save(tramo);
    }

    @GetMapping("/{id}")
    public Tramo obtenerPorId(@PathVariable Long id) {
        return tramoRepository.findById(id).orElse(null);
    }
}


