package com.tpi.repository;

import com.tpi.model.Tramo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {

    // Buscar todos los tramos por ruta ID
    List<Tramo> findByRutaId(Long rutaId);
    
    // Con ordenamiento
    List<Tramo> findByRutaIdOrderByOrdenAsc(Long rutaId);
    
    // Con @Query explícito
    @Query("SELECT t FROM Tramo t WHERE t.ruta.id = :rutaId")
    List<Tramo> findTramosByRutaId(@Param("rutaId") Long rutaId);
}
