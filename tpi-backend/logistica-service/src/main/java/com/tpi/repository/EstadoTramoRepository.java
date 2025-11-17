package com.tpi.repository;

import com.tpi.model.EstadoTramo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoTramoRepository extends JpaRepository<EstadoTramo, Long> {

    Optional<EstadoTramo> findByNombre(String nombre);
}

