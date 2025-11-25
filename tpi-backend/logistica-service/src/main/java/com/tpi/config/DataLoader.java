package com.tpi.config;

import com.tpi.model.*;
import com.tpi.repository.*;
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

    private final TipoUbicacionRepository tipoUbicacionRepository;
    private final TipoTramoRepository tipoTramoRepository;
    private final EstadoTramoRepository estadoTramoRepository;
    private final CamionRepository camionRepository;
    private final DepositoRepository depositoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final TarifaRepository tarifaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Iniciando carga de datos por defecto...");
            cargarTiposUbicacion();
            cargarTiposTramo();
            cargarEstadosTramo();
            cargarCamionesPrueba();
            cargarDepositosPrueba();
            cargarTarifasAproximadas();
            
            log.info("Carga de datos completada exitosamente");
        } catch (Exception e) {
            log.error("Error cargando datos por defecto: {}", e.getMessage(), e);
        }
    }

    /**
     *     Tarifas diferencias basadas en los volumenes de cada contenedor asignado
     */
    @SuppressWarnings("null")
    private void cargarTarifasAproximadas() {
        List<Tarifa> tarifas = Arrays.asList(
            // CONTENEDORES PEQUEÑOS (0-20m³)
            Tarifa.builder()
                .nombre("Tarifa Pequeño - 0-20m³")
                .descripcion("Para contenedores pequeños de hasta 20m³. Ideal para viviendas básicas y oficinas temporales. Camiones económicos con menor consumo.")
                .volumenMin(0.0)
                .volumenMax(20.0)
                .costoGestionPorTramo(400.0)
                .precioCombustiblePorLitro(250.0)
                .build(),
                
            // CONTENEDORES MEDIANOS (20-40m³)
            Tarifa.builder()
                .nombre("Tarifa Mediano - 20-40m³")
                .descripcion("Para contenedores medianos de 20-40m³. Ideal para viviendas familiares estándar. Balance perfecto entre costo y capacidad.")
                .volumenMin(20.0)
                .volumenMax(40.0)
                .costoGestionPorTramo(500.0)
                .precioCombustiblePorLitro(250.0)
                .build(),
                
            // CONTENEDORES GRANDES (40-70m³)
            Tarifa.builder()
                .nombre("Tarifa Grande - 40-70m³")
                .descripcion("Para contenedores grandes de 40-70m³. Ideal para viviendas amplias y proyectos especiales. Camiones de alta capacidad.")
                .volumenMin(40.0)
                .volumenMax(70.0)
                .costoGestionPorTramo(600.0)
                .precioCombustiblePorLitro(250.0)
                .build(),
                
            // CONTENEDORES EXTRA GRANDES (70+m³)
            Tarifa.builder()
                .nombre("Tarifa Extra Grande - 70+m³")
                .descripcion("Para contenedores extra grandes mayores a 70m³. Proyectos especiales y contenedores con equipamiento incluido. Transporte especializado.")
                .volumenMin(70.0)
                .volumenMax(1000.0)
                .costoGestionPorTramo(800.0)
                .precioCombustiblePorLitro(300.0)
                .build()
        );
        
        tarifaRepository.saveAll(tarifas);
        log.info("{} tarifas aproximadas creadas", tarifas.size());
    }

    /**
     * Carga de positos de prueba, maybe nos hagan modificar esto en presentacion
     */
    @SuppressWarnings("null")
    private void cargarDepositosPrueba() {
        if (depositoRepository.count() == 0) {
            // Primero necesitamos crear algunas ubicaciones para los depósitos
            List<Ubicacion> ubicacionesDepositos = Arrays.asList(
                Ubicacion.builder()
                    .direccion("Av. Industrial 1234, Parque Industrial Pilar")
                    .nombre("Depósito Pilar")
                    .latitud(-34.4736)
                    .longitud(-58.9132)
                    .tipo(tipoUbicacionRepository.findByNombre("DEPOSITO").get())
                    .build(),
                
                Ubicacion.builder()
                    .direccion("Ruta 8 Km 45, Luján")
                    .nombre("Depósito Luján")
                    .latitud(-34.5575)
                    .longitud(-59.1200)
                    .tipo(tipoUbicacionRepository.findByNombre("DEPOSITO").get())
                    .build(),
                
                Ubicacion.builder()
                    .direccion("Autopista Buenos Aires - La Plata Km 25")
                    .nombre("Depósito Quilmes")
                    .latitud(-34.7244)
                    .longitud(-58.2520)
                    .tipo(tipoUbicacionRepository.findByNombre("DEPOSITO").get())
                    .build()
            );
            
            ubicacionRepository.saveAll(ubicacionesDepositos);
            
            // Ahora crear los depósitos
            List<Deposito> depositos = Arrays.asList(
                Deposito.builder()
                    .nombre("Depósito Central Pilar")
                    .costoEstadiaPorDia(2500.0)
                    .ubicacion(ubicacionesDepositos.get(0))
                    .build(),
                
                Deposito.builder()
                    .nombre("Depósito Zona Norte Luján")
                    .costoEstadiaPorDia(1800.0)
                    .ubicacion(ubicacionesDepositos.get(1))
                    .build(),
                
                Deposito.builder()
                    .nombre("Depósito Zona Sur Quilmes")
                    .costoEstadiaPorDia(2200.0)
                    .ubicacion(ubicacionesDepositos.get(2))
                    .build()
            );

            depositoRepository.saveAll(depositos);
            log.info("{} depósitos de prueba creados", depositos.size());
        }
    }

    /**
     * crea camiones de prueba capaz nos toca cambiar
     */
    @SuppressWarnings("null")
    private void cargarCamionesPrueba() {
        // Solo crear camiones si no existen
        if (camionRepository.count() == 0) {
            List<Camion> camiones = Arrays.asList(
                // Camión pequeño - para contenedores livianos
                Camion.builder()
                    .dominio("ABC123")
                    .modelo("Mercedes-Benz Atego 1017")
                    .capacidadPesoKg(5000.0)
                    .capacidadVolumenM3(30.0)
                    .disponible(true)
                    .costoPorKm(150.0)
                    .consumoCombustibleLx100km(18.5)
                    .nombreConductor("Juan Pérez")
                    .telefonoConductor("+5491112345678")
                    .build(),
                
                // Camión mediano - uso general
                Camion.builder()
                    .dominio("DEF456")
                    .modelo("Volvo FM 1323")
                    .capacidadPesoKg(12000.0)
                    .capacidadVolumenM3(60.0)
                    .disponible(true)
                    .costoPorKm(220.0)
                    .consumoCombustibleLx100km(25.0)
                    .nombreConductor("María García")
                    .telefonoConductor("+549115554321")
                    .build(),
                
                // Camión grande - para contenedores pesados/grandes
                Camion.builder()
                    .dominio("GHI789")
                    .modelo("Scania R730")
                    .capacidadPesoKg(25000.0)
                    .capacidadVolumenM3(90.0)
                    .disponible(true)
                    .costoPorKm(350.0)
                    .consumoCombustibleLx100km(32.5)
                    .nombreConductor("Carlos López")
                    .telefonoConductor("+549116667788")
                    .build(),
                
                // Camión fuera de servicio
                Camion.builder()
                    .dominio("JKL012")
                    .modelo("Iveco Stralis")
                    .capacidadPesoKg(18000.0)
                    .capacidadVolumenM3(75.0)
                    .disponible(false) // No disponible
                    .costoPorKm(280.0)
                    .consumoCombustibleLx100km(28.0)
                    .nombreConductor("Ana Martínez")
                    .telefonoConductor("+549119998877")
                    .build()
            );

            camionRepository.saveAll(camiones);
            log.info("{} camiones de prueba creados", camiones.size());
        }
    }

    /**
     * carga clases de ayuda 
     */
    @SuppressWarnings("null")
    private void cargarTiposUbicacion() {
        List<String> tipos = Arrays.asList("DEPOSITO", "ORIGEN", "DESTINO", "CLIENTE");
        
        for (String nombre : tipos) {
            if (tipoUbicacionRepository.findByNombre(nombre).isEmpty()) {
                TipoUbicacion tipo = TipoUbicacion.builder()
                    .nombre(nombre)
                    .descripcion("Tipo de ubicación: " + nombre)
                    .build();
                tipoUbicacionRepository.save(tipo);
                log.info("Tipo ubicación creado: {}", nombre);
            }
        }
    }

    @SuppressWarnings("null")
    private void cargarTiposTramo() {
        List<String> tipos = Arrays.asList(
            "ORIGEN_DEPOSITO", 
            "DEPOSITO_DEPOSITO", 
            "DEPOSITO_DESTINO", 
            "ORIGEN_DESTINO"
        );
        
        for (String nombre : tipos) {
            if (tipoTramoRepository.findByNombre(nombre).isEmpty()) {
                TipoTramo tipo = TipoTramo.builder()
                    .nombre(nombre)
                    .descripcion("Tramo desde " + nombre.replace("_", " a "))
                    .build();
                tipoTramoRepository.save(tipo);
                log.info("Tipo tramo creado: {}", nombre);
            }
        }
    }

    @SuppressWarnings("null")
    private void cargarEstadosTramo() {
        List<String> estados = Arrays.asList("ESTIMADO", "ASIGNADO", "INICIADO", "FINALIZADO");
        
        for (String nombre : estados) {
            if (estadoTramoRepository.findByNombre(nombre).isEmpty()) {
                EstadoTramo estado = EstadoTramo.builder()
                    .nombre(nombre)
                    .descripcion("Estado del tramo: " + nombre)
                    .build();
                estadoTramoRepository.save(estado);
                log.info("Estado de tramo creado: {}", nombre);
            }
        }
    }
}