package com.tpi.repository;

import com.tpi.model.TipoUbicacion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoUbicacionRepository extends JpaRepository<TipoUbicacion, Long> {

    Optional<TipoUbicacion> findByNombre(String nombre);
}
