package com.tpi.controller;

import com.tpi.dto.response.ContenedorResponseDTO;

import com.tpi.service.ContenedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contenedores")
@RequiredArgsConstructor
public class ContenedorController {

    private final ContenedorService contenedorService;

    /**
     * GET - Obtener todos los contenedores
     * # Todos los contenedores
GET /api/v1/contenedores

# Contenedores filtrados por estado
GET /api/v1/contenedores?estado=DISPONIBLE
GET /api/v1/contenedores?estado=EN_TRANSITO
GET /api/v1/contenedores?estado=ENTREGADO
     */
    @GetMapping
    public ResponseEntity<List<ContenedorResponseDTO>> obtenerTodosContenedores(
            @RequestParam(required = false) String estado) {
        List<ContenedorResponseDTO> contenedores = contenedorService.findAll(estado);
        return ResponseEntity.ok(contenedores);
    }

    /**
     * GET - Obtener contenedor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContenedorResponseDTO> obtenerContenedor(@PathVariable Long id) {
        ContenedorResponseDTO contenedor = contenedorService.findById(id);
        return ResponseEntity.ok(contenedor);
    }

    /**
     * PATCH - Actualizar estado de un contenedor
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ContenedorResponseDTO> actualizarEstadoContenedor(
            @PathVariable Long id,
            @RequestParam String estado) {
        ContenedorResponseDTO contenedor = contenedorService.actualizarEstado(id, estado);
        return ResponseEntity.ok(contenedor);
    }
}