package com.tpi.repository;

import com.tpi.model.TipoUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoUbicacionRepository extends JpaRepository<TipoUbicacion, Long> {
}
