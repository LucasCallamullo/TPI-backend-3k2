package com.tpi.repository;

import com.tpi.model.Ruta;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    Optional<Ruta> findBySolicitudId(Long solicitudId);
}


