package com.tpi.config;

import com.tpi.model.EstadoSolicitud;
import com.tpi.model.EstadoContenedor;
import com.tpi.repository.EstadoSolicitudRepository;
import com.tpi.repository.EstadoContenedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j  // Para logging con Lombok
public class DataLoader implements CommandLineRunner {

    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final EstadoContenedorRepository estadoContenedorRepository;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Iniciando carga de datos por defecto...");
            cargarEstadosSolicitud();
            cargarEstadosContenedor();
            log.info("Carga de datos completada exitosamente");
        } catch (Exception e) {
            log.error("Error cargando datos por defecto: {}", e.getMessage());
        }
    }

    @SuppressWarnings("null")
    private void cargarEstadosSolicitud() {
        List<String> estados = Arrays.asList("Borrador", "Programada", "En Tránsito", "Entregada");
        
        for (String nombre : estados) {
            if (estadoSolicitudRepository.findByNombre(nombre).isEmpty()) {
                EstadoSolicitud estado = EstadoSolicitud.builder().nombre(nombre).build();
                estadoSolicitudRepository.save(estado);
                log.info("Estado solicitud creado: {}", nombre);
            }
        }
    }

    @SuppressWarnings("null")
    private void cargarEstadosContenedor() {
        List<String> estados = Arrays.asList("Disponible", "En Tránsito", "Entregado", "En Deposito");
        
        for (String nombre : estados) {
            if (estadoContenedorRepository.findByNombre(nombre).isEmpty()) {
                EstadoContenedor estado = EstadoContenedor.builder().nombre(nombre).build();
                estadoContenedorRepository.save(estado);
                log.info("Estado contenedor creado: {}", nombre);
            }
        }
    }
}