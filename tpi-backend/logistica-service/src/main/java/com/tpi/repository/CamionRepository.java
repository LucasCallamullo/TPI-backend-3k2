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

    // CamionRepository
    Optional<Camion> findByDominio(String dominio);
    
    List<Camion> findByDisponibleTrue();

    /* metodos opcionales para querys probar */
    @Query("SELECT c FROM Camion c WHERE c.capacidadPesoKg >= :pesoRequerido AND c.capacidadVolumenM3 >= :volumenRequerido AND c.disponible = true")
    List<Camion> findByCapacidadesSuficientes(@Param("pesoRequerido") Double pesoRequerido, 
                                            @Param("volumenRequerido") Double volumenRequerido);

    // Query alternativa que incluye camiones no disponibles
    @Query("SELECT c FROM Camion c WHERE c.capacidadPesoKg >= :pesoRequerido AND c.capacidadVolumenM3 >= :volumenRequerido")
    List<Camion> findByCapacidadesSuficientesIncluyendoNoDisponibles(@Param("pesoRequerido") Double pesoRequerido, 
                                                                    @Param("volumenRequerido") Double volumenRequerido);
    
    // Método automático de Spring Data (muy largo pero funcional)
    List<Camion> findByCapacidadPesoKgGreaterThanEqualAndCapacidadVolumenM3GreaterThanEqualAndDisponibleTrue(
        Double capacidadPesoKg, Double capacidadVolumenM3);

    List<Camion> findByCapacidadPesoKgGreaterThanEqualAndCapacidadVolumenM3GreaterThanEqual(
        Double peso, Double volumen);
}

