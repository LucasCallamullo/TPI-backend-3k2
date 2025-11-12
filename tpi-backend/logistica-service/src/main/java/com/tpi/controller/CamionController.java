package com.tpi.controller;

import com.tpi.model.Camion;
import com.tpi.repository.CamionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/camiones")
public class CamionController {

    private final CamionRepository camionRepository;

    public CamionController(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    @GetMapping
    public List<Camion> listarCamiones() {
        return camionRepository.findAll();
    }

    @PostMapping
    public Camion crearCamion(@RequestBody Camion camion) {
        return camionRepository.save(camion);
    }

    @GetMapping("/{id}")
    public Camion obtenerPorId(@PathVariable Long id) {
        return camionRepository.findById(id).orElse(null);
    }
}
