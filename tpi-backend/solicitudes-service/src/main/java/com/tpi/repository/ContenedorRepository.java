package com.tpi.repository;

import com.tpi.model.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
    Optional<Contenedor> findByIdentificacionUnica(String identificacionUnica);
    boolean existsByIdentificacionUnica(String identificacionUnica);

    // Método 1: Usando Spring Data JPA Query Creation
    // Busca por el nombre del estado (relación EstadoContenedor -> nombre)
    List<Contenedor> findByEstadoNombre(String nombreEstado);
    
    // Método 2: Busca contenedores donde el estado NO sea el especificado
    List<Contenedor> findByEstadoNombreNot(String nombreEstado);
    
    // Cargar todo en una sola query para evitar problema n+1
    @Query("SELECT c FROM Contenedor c JOIN FETCH c.estado")
    List<Contenedor> findAllWithEstado();
    
    @Query("SELECT c FROM Contenedor c JOIN FETCH c.estado WHERE c.id = :id")
    Optional<Contenedor> findByIdWithEstado(@Param("id") Long id);
    
    @Query("SELECT c FROM Contenedor c JOIN FETCH c.estado WHERE c.estado.nombre = :estado")
    List<Contenedor> findByEstadoNombreWithEstado(@Param("estado") String estado);
}
