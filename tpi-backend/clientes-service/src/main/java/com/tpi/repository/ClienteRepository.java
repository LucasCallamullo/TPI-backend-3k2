package com.tpi.repository;

import com.tpi.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Cliente.
 * 
 * Extiende JpaRepository que proporciona operaciones CRUD automáticas
 * sin necesidad de implementar métodos básicos.
 * 
 * Parámetros genéricos:
 * - Cliente: La entidad que gestiona este repositorio
 * - String: El tipo de la clave primaria (id del cliente = keycloakId)
 */
@Repository  // Marca esta interfaz como un componente de repositorio de Spring
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    /* 
    No necesitas declarar métodos aquí - Spring Data JPA los provee automáticamente
    Algunos ejemplos:

    save(Cliente entity) - Guardar o actualizar
    findById(String id) - Buscar por ID (keycloakId)
    findAll() - Obtener todos los clientes
    deleteById(String id) - Eliminar por ID
    count() - Contar total de clientes
    existsById(String id) - Verificar si existe 
    */
}