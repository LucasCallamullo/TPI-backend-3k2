package com.tpi.repository;

import com.tpi.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByClienteId(String clienteId);
    List<Solicitud> findByEstadoNombre(String estadoNombre);
    boolean existsByContenedorIdentificacionUnica(String identificacionUnica);

    // Consulta específica que combina búsqueda + validación de acceso
    Optional<Solicitud> findByIdAndClienteId(Long id, String clienteId);
}