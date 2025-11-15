package com.tpi.controller;

import com.tpi.model.Deposito;
import com.tpi.repository.DepositoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/depositos")
public class DepositoController {

    private final DepositoRepository depositoRepository;

    public DepositoController(DepositoRepository depositoRepository) {
        this.depositoRepository = depositoRepository;
    }

    @GetMapping
    public List<Deposito> listarDepositos() {
        return depositoRepository.findAll();
    }

    @PostMapping
    public Deposito crearDeposito(@RequestBody Deposito deposito) {
        return depositoRepository.save(deposito);
    }

    @GetMapping("/{id}")
    public Deposito obtenerPorId(@PathVariable Long id) {
        return depositoRepository.findById(id).orElse(null);
    }
}

