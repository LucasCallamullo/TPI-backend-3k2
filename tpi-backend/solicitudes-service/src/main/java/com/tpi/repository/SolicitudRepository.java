package com.tpi.repository;

import com.tpi.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByClienteId(String clienteId);
    List<Solicitud> findByEstadoNombre(String estadoNombre);
    boolean existsByContenedorIdentificacionUnica(String identificacionUnica);
}