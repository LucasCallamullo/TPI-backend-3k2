package com.tpi.controller;

import com.tpi.model.Ubicacion;
import com.tpi.repository.UbicacionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ubicaciones")
public class UbicacionController {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionController(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @GetMapping
    public List<Ubicacion> listarUbicaciones() {
        return ubicacionRepository.findAll();
    }

    @PostMapping
    public Ubicacion crearUbicacion(@RequestBody Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    @GetMapping("/{id}")
    public Ubicacion obtenerPorId(@PathVariable Long id) {
        return ubicacionRepository.findById(id).orElse(null);
    }
}