package com.tpi.repository;

import com.tpi.model.TipoTramo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoTramoRepository extends JpaRepository<TipoTramo, Long> {

    Optional<TipoTramo> findByNombre(String nombre);
}

