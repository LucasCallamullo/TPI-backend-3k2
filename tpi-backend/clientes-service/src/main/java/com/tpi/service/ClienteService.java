package com.tpi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tpi.dto.ClienteDTO;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Cliente;
import com.tpi.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * Sincroniza un cliente con la base de datos.
     * 
     * Si el cliente ya existe (por keycloakId), actualiza sus datos solo si cambian.
     * Si no existe, lo crea con los datos proporcionados.
     *
     * @param keycloakId ID único del cliente en Keycloak
     * @param nombre Nombre del cliente
     * @param email Email del cliente
     * @param telefono Teléfono del cliente
     * @param direccion Dirección del cliente
     * @return El cliente actualizado o creado
     */
    @SuppressWarnings("null")
    public Cliente sincronizarCliente(
            String keycloakId, String nombre, String email, String telefono, String direccion) {

        // Buscar si el cliente ya existe en la base de datos
        return clienteRepository.findById(keycloakId)
            .map(clienteExistente -> {
                boolean updated = false; // bandera para saber si hubo cambios

                // Paso 1: Actualizar nombre si cambió
                if (nombre != null && !nombre.equals(clienteExistente.getNombre())) {
                    clienteExistente.setNombre(nombre);
                    updated = true;
                }

                // Paso 2: Actualizar email si cambió
                if (email != null && !email.equals(clienteExistente.getEmail())) {
                    clienteExistente.setEmail(email);
                    updated = true;
                }

                // Paso 3: Actualizar teléfono si cambió
                if (telefono != null && !telefono.equals(clienteExistente.getTelefono())) {
                    clienteExistente.setTelefono(telefono);
                    updated = true;
                }

                // Paso 4: Actualizar dirección si cambió
                if (direccion != null && !direccion.equals(clienteExistente.getDireccion())) {
                    clienteExistente.setDireccion(direccion);
                    updated = true;
                }

                // Paso 5: Guardar cambios solo si hubo alguna actualización
                return updated ? clienteRepository.save(clienteExistente) : clienteExistente;
            })
            .orElseGet(() -> {
                // Paso 6: Si el cliente no existe, crear uno nuevo
                Cliente nuevoCliente = Cliente.builder()
                    .id(keycloakId)
                    .nombre(nombre)
                    .email(email != null ? email : "")
                    .telefono(telefono != null ? telefono : "")
                    .direccion(direccion != null ? direccion : "")
                    .fechaCreacion(LocalDateTime.now()) // registrar fecha de creación
                    .build();

                // Paso 7: Guardar el nuevo cliente en la base de datos
                return clienteRepository.save(nuevoCliente);
            });
    }


    /**
     * Obtiene un ClienteDTO a partir del ID del cliente.
     * Primero busca el cliente en la base de datos y luego convierte
     * la entidad encontrada al DTO correspondiente.
     *
     * @param id ID único del cliente (keycloakId)
     * @return DTO con la información del cliente
     * @throws EntidadNotFoundException si no existe un cliente con el ID dado
     */
    public ClienteDTO getDTOClienteById(String id) {
        Cliente e = this.getClienteById(id);
        return ClienteDTO.fromEntity(e);
    }

    /**
     * Busca un cliente por su ID.
     * Si el cliente no existe en la base de datos, lanza una excepción específica.
     *
     * @param id ID único del cliente (keycloakId)
     * @return la entidad Cliente correspondiente al ID
     * @throws EntidadNotFoundException si no se encuentra el cliente solicitado
     */
    @SuppressWarnings("null")
    public Cliente getClienteById(String id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("Cliente no encontrado con id: ", id));
    }

    /**
     * Obtiene todos los clientes registrados en la base de datos.
     *
     * @return lista completa de entidades Cliente
     */
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }
}