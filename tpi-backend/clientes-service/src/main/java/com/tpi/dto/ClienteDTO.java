package com.tpi.dto;

import com.tpi.model.Cliente;

public record ClienteDTO(
    String id,
    String nombre,
    String email,
    String telefono,
    String direccion
) {
    /**
     * Convierte una entidad Cliente a un ClienteDTO.
     * 
     * @param cliente entidad Cliente obtenida desde la base de datos
     * @return una instancia de ClienteDTO
     */
    public static ClienteDTO fromEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        return new ClienteDTO(
            cliente.getId(),
            cliente.getNombre(),
            cliente.getEmail(),
            cliente.getTelefono(),
            cliente.getDireccion()
        );
    }
}

