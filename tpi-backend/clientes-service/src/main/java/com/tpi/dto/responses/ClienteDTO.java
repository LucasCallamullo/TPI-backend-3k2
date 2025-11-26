package com.tpi.dto.responses;

import com.tpi.model.Cliente;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO que representa información de un cliente.
 */
public record ClienteDTO(
    @Schema(description = "ID del cliente (keycloakId)", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,

    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez")
    String nombre,

    @Schema(description = "Email del cliente", example = "juan.perez@email.com")
    String email,

    @Schema(description = "Teléfono de contacto", example = "+54 9 11 1234-5678")
    String telefono,

    @Schema(description = "Dirección principal del cliente", example = "Av. Corrientes 1234, CABA")
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