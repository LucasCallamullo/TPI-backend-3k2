package com.tpi.controller;

import com.tpi.model.TipoTramo;
import com.tpi.service.TipoTramoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipo-tramos")
public class TipoTramoController {

    private final TipoTramoService tipoTramoService;

    public TipoTramoController(TipoTramoService tipoTramoService) {
        this.tipoTramoService = tipoTramoService;
    }

    @GetMapping
    public List<TipoTramo> listarTipoTramos() {
        return tipoTramoService.findAll();
    }

    @SuppressWarnings("null")
    @PostMapping
    public TipoTramo crearTipoTramo(@RequestBody TipoTramo tipoTramo) {
        return tipoTramoService.save(tipoTramo);
    }

    @SuppressWarnings("null")
    @GetMapping("/{id}")
    public TipoTramo findById(@PathVariable Long id) {
        return tipoTramoService.findById(id);
    }
}
