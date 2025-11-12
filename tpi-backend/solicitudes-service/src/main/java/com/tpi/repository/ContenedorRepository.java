package com.tpi.repository;

import com.tpi.model.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
    Optional<Contenedor> findByIdentificacionUnica(String identificacionUnica);
    boolean existsByIdentificacionUnica(String identificacionUnica);
}
