package com.tpi.controller;

import com.tpi.model.Ruta;
import com.tpi.repository.RutaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rutas")
public class RutaController {

    private final RutaRepository rutaRepository;

    public RutaController(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    @GetMapping
    public List<Ruta> listarRutas() {
        return rutaRepository.findAll();
    }

    @PostMapping
    public Ruta crearRuta(@RequestBody Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    @GetMapping("/{id}")
    public Ruta obtenerPorId(@PathVariable Long id) {
        return rutaRepository.findById(id).orElse(null);
    }
}
