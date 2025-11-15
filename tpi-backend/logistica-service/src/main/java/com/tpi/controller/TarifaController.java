package com.tpi.controller;

import com.tpi.model.Tarifa;
import com.tpi.repository.TarifaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tarifas")
public class TarifaController {

    private final TarifaRepository tarifaRepository;

    public TarifaController(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    @GetMapping
    public List<Tarifa> listarTarifas() {
        return tarifaRepository.findAll();
    }

    @PostMapping
    public Tarifa crearTarifa(@RequestBody Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }

    @GetMapping("/{id}")
    public Tarifa obtenerPorId(@PathVariable Long id) {
        return tarifaRepository.findById(id).orElse(null);
    }
}


