package com.tpi.config;

import com.tpi.model.EstadoSolicitud;
import com.tpi.model.Contenedor;
import com.tpi.model.EstadoContenedor;
import com.tpi.repository.EstadoSolicitudRepository;
import com.tpi.repository.ContenedorRepository;
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
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final EstadoContenedorRepository estadoContenedorRepository;
    private final ContenedorRepository contenedorRepository;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Iniciando carga de datos por defecto...");
            cargarEstadosSolicitud();
            cargarEstadosContenedor();
            cargarContenedoresPrueba();

            log.info("Carga de datos completada exitosamente");
        } catch (Exception e) {
            log.error("Error cargando datos por defecto: {}", e.getMessage());
        }
    }

    /**
     * Carga contenedores de prueba capaz nos pidan modificar esto en presentacion
     */
    @SuppressWarnings("null")
    private void cargarContenedoresPrueba() {
        // Solo crear contenedores si no existen
        if (contenedorRepository.count() == 0) {
            // Obtener estados necesarios con manejo de Optional
            EstadoContenedor estadoDisponible = estadoContenedorRepository.findByNombre("DISPONIBLE")
                .orElseThrow(() -> new RuntimeException("Estado DISPONIBLE no encontrado"));
            
            EstadoContenedor estadoEnDeposito = estadoContenedorRepository.findByNombre("EN_DEPOSITO")
                .orElseThrow(() -> new RuntimeException("Estado EN_DEPOSITO no encontrado"));
            
            List<Contenedor> contenedores = Arrays.asList(
                // Contenedor pequeño - para viviendas pequeñas
                Contenedor.builder()
                    .identificacionUnica("CONT-2024-001")
                    .peso(1200.0)
                    .volumen(15.0)
                    .descripcion("Contenedor pequeño para vivienda básica")
                    .estado(estadoDisponible)
                    .build(),
                
                // Contenedor mediano - uso estándar
                Contenedor.builder()
                    .identificacionUnica("CONT-2024-002")
                    .peso(2500.0)
                    .volumen(30.0)
                    .descripcion("Contenedor estándar para vivienda familiar")
                    .estado(estadoDisponible)
                    .build(),
                
                // Contenedor grande - para viviendas amplias
                Contenedor.builder()
                    .identificacionUnica("CONT-2024-003")
                    .peso(3800.0)
                    .volumen(45.0)
                    .descripcion("Contenedor grande para vivienda amplia")
                    .estado(estadoEnDeposito)
                    .build(),
                
                // Contenedor extra grande - proyectos especiales
                Contenedor.builder()
                    .identificacionUnica("CONT-2024-004")
                    .peso(5200.0)
                    .volumen(60.0)
                    .descripcion("Contenedor extra grande para proyecto especial")
                    .estado(estadoDisponible)
                    .build(),
                
                // Contenedor liviano - oficinas temporales
                Contenedor.builder()
                    .identificacionUnica("CONT-2024-005")
                    .peso(800.0)
                    .volumen(12.0)
                    .descripcion("Contenedor liviano para oficina temporal")
                    .estado(estadoDisponible)
                    .build(),
                
                // Contenedor pesado - con equipamiento incluido
                Contenedor.builder()
                    .identificacionUnica("CONT-2024-006")
                    .peso(6500.0)
                    .volumen(35.0)
                    .descripcion("Contenedor con equipamiento de cocina y baño")
                    .estado(estadoEnDeposito)
                    .build()
            );

            contenedorRepository.saveAll(contenedores);
            log.info("{} contenedores de prueba creados", contenedores.size());
        }
    }

    @SuppressWarnings("null")
    private void cargarEstadosSolicitud() {
        List<String> estados = Arrays.asList("BORRADOR", "PROGRAMADA", "EN_TRANSITO", "ENTREGADA");
        
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
        List<String> estados = Arrays.asList("DISPONIBLE", "ASIGNADO", "EN_TRANSITO", "ENTREGADO", "EN_DEPOSITO");
        
        for (String nombre : estados) {
            if (estadoContenedorRepository.findByNombre(nombre).isEmpty()) {
                EstadoContenedor estado = EstadoContenedor.builder().nombre(nombre).build();
                estadoContenedorRepository.save(estado);
                log.info("Estado contenedor creado: {}", nombre);
            }
        }
    }
}