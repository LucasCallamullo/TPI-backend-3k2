package com.tpi.repository;

import com.tpi.model.EstadoContenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoContenedorRepository extends JpaRepository<EstadoContenedor, Long> {
    Optional<EstadoContenedor> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}