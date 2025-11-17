package com.tpi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tpi.model.Cliente;
import com.tpi.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Constructor que inyecta el repositorio mediante inyección de dependencias
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    @SuppressWarnings("null")
    public Cliente sincronizarCliente(
        String keycloakId, String nombre, String email, String telefono, String direccion) {

        return clienteRepository.findById(keycloakId)
            .map(clienteExistente -> {
                boolean updated = false;
                
                if (nombre != null && !nombre.equals(clienteExistente.getNombre())) {
                    clienteExistente.setNombre(nombre);
                    updated = true;
                }
                if (email != null && !email.equals(clienteExistente.getEmail())) {
                    clienteExistente.setEmail(email);
                    updated = true;
                }
                if (telefono != null && !telefono.equals(clienteExistente.getTelefono())) {
                    clienteExistente.setTelefono(telefono);
                    updated = true;
                }
                if (direccion != null && !direccion.equals(clienteExistente.getDireccion())) {
                    clienteExistente.setDireccion(direccion);
                    updated = true;
                }
                
                return updated ? clienteRepository.save(clienteExistente) : clienteExistente;
            })
            .orElseGet(() -> {
                Cliente nuevoCliente = Cliente.builder()
                    .id(keycloakId)
                    .nombre(nombre)
                    .email(email != null ? email : "")
                    .telefono(telefono != null ? telefono : "")
                    .direccion(direccion != null ? direccion : "")
                    .fechaCreacion(LocalDateTime.now())
                    .build();
                return clienteRepository.save(nuevoCliente);
            });
    }

    /* 
    @SuppressWarnings("null")
    public Cliente sincronizarCliente(String keycloakId, String nombre, String email, String telefono) {
        
        // Buscar si el cliente ya existe en la base de datos usando el keycloakId
        Optional<Cliente> clienteExistente = clienteRepository.findById(keycloakId);
        
        if (clienteExistente.isPresent()) {
            // Si el cliente existe, obtener la instancia
            Cliente cliente = clienteExistente.get();
            
            // Bandera para controlar si se realizaron actualizaciones
            boolean updated = false;
            
            // Actualizar el nombre solo si es diferente y no es nulo
            if (nombre != null && !nombre.equals(cliente.getNombre())) {
                cliente.setNombre(nombre);
                updated = true;
            }
            
            // Actualizar el email solo si es diferente y no es nulo
            if (email != null && !email.equals(cliente.getEmail())) {
                cliente.setEmail(email);
                updated = true;
            }
            
            // Actualizar el teléfono solo si es diferente y no es nulo
            if (telefono != null && !telefono.equals(cliente.getTelefono())) {
                cliente.setTelefono(telefono);
                updated = true;
            }
            
            // Guardar los cambios solo si hubo actualizaciones
            if (updated) {
                return clienteRepository.save(cliente);
            }
            
            // Retornar el cliente sin cambios si no hubo actualizaciones
            return cliente;
            
        } else {
            // Si el cliente no existe, crear uno nuevo
            Cliente nuevoCliente = Cliente.builder()
                .id(keycloakId)  // Usar el keycloakId como ID primario
                .nombre(nombre)  // Nombre es obligatorio según tu modelo
                .email(email != null ? email : "")  // Si email es nulo, usar string vacío
                .telefono(telefono != null ? telefono : "")  // Si teléfono es nulo, usar string vacío
                .direccion("")  // Dirección vacía por defecto
                .fechaCreacion(LocalDateTime.now())  // Fecha actual como fecha de creación
                .build();
            
            // Guardar el nuevo cliente en la base de datos
            return clienteRepository.save(nuevoCliente);
        }
    } */

    @SuppressWarnings("null")
    // Método para buscar un cliente por su ID (keycloakId)
    public Optional<Cliente> getClienteById(String id) {
        return clienteRepository.findById(id);
    }

    // Método para obtener todos los clientes de la base de datos
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }
}