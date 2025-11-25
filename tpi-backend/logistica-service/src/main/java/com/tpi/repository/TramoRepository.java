package com.tpi.repository;

import com.tpi.model.Tramo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {

    // Busca todos los tramos que pertenecen a una ruta según su ID.
    // No garantiza ningún orden específico.
    List<Tramo> findByRutaId(Long rutaId);

    // Busca todos los tramos de una ruta según su ID,
    // ordenados por el campo "orden" de manera ascendente.
    List<Tramo> findByRutaIdOrderByOrdenAsc(Long rutaId);

    // Busca todos los tramos de una ruta usando una consulta JPQL explícita.
    // Equivalente al método findByRutaId, pero permite personalización futura.
    @Query("SELECT t FROM Tramo t WHERE t.ruta.id = :rutaId")
    List<Tramo> findTramosByRutaId(@Param("rutaId") Long rutaId);

}
