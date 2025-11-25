package com.tpi.repository;

import com.tpi.model.Camion;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {

    /**
     * Busca un camión por su dominio (patente).
     *
     * @param dominio dominio único del camión.
     * @return un Optional que contiene el camión si existe.
     */
    Optional<Camion> findByDominio(String dominio);

    /**
     * Obtiene todos los camiones que están marcados como disponibles.
     *
     * @return lista de camiones disponibles.
     */
    List<Camion> findByDisponibleTrue();

    /**
     * Busca camiones que tengan capacidad suficiente de peso y volumen,
     * y que además estén disponibles.
     *
     * @param pesoRequerido peso mínimo requerido (kg).
     * @param volumenRequerido volumen mínimo requerido (m³).
     * @return lista de camiones que cumplen con los requisitos.
     */
    @Query("SELECT c FROM Camion c WHERE c.capacidadPesoKg >= :pesoRequerido AND c.capacidadVolumenM3 >= :volumenRequerido AND c.disponible = true")
    List<Camion> findByCapacidadesSuficientes(@Param("pesoRequerido") Double pesoRequerido, 
                                            @Param("volumenRequerido") Double volumenRequerido);

    /**
     * Versión alternativa de la consulta que NO filtra por camiones disponibles.
     * Devuelve camiones suficientes estén o no disponibles.
     *
     * @param pesoRequerido peso mínimo requerido (kg).
     * @param volumenRequerido volumen mínimo requerido (m³).
     * @return lista de camiones que cumplen con sus capacidades.
     */
    @Query("SELECT c FROM Camion c WHERE c.capacidadPesoKg >= :pesoRequerido AND c.capacidadVolumenM3 >= :volumenRequerido")
    List<Camion> findByCapacidadesSuficientesIncluyendoNoDisponibles(@Param("pesoRequerido") Double pesoRequerido, 
                                                                    @Param("volumenRequerido") Double volumenRequerido);

    /**
     * Consulta automática generada por Spring Data.
     * Busca camiones con capacidades suficientes por peso y volumen.
     *
     * @param peso peso mínimo requerido (kg).
     * @param volumen volumen mínimo requerido (m³).
     * @return lista de camiones que cumplen las capacidades.
     */
    List<Camion> findByCapacidadPesoKgGreaterThanEqualAndCapacidadVolumenM3GreaterThanEqual(
            Double peso, Double volumen);



    // Método automático de Spring Data (muy largo pero funcional)
    // List<Camion> findByCapacidadPesoKgGreaterThanEqualAndCapacidadVolumenM3GreaterThanEqualAndDisponibleTrue(
    //    Double capacidadPesoKg, Double capacidadVolumenM3);
}

