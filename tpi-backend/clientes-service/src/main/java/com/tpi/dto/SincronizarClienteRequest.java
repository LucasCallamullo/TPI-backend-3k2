package com.tpi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@Schema(description = "DTO para solicitud de sincronización de cliente desde Keycloak")
public class SincronizarClienteRequest {
    
    @Schema(
        description = "ID único del usuario en Keycloak",
        example = "123e4567-e89b-12d3-a456-426614174000",
        required = true
    )
    @NotBlank(message = "keycloakId es requerido")
    private String keycloakId;

    @Schema(
        description = "Nombre completo del cliente",
        example = "Juan Pérez",
        required = true
    )
    @NotBlank(message = "nombre es requerido")
    private String nombre;

    @Schema(
        description = "Email del cliente",
        example = "juan.perez@email.com"
    )
    @Email(message = "El formato del email es inválido")
    private String email;

    @Schema(
        description = "Teléfono del cliente",
        example = "+5491123456789"
    )
    private String telefono;
    
    // Constructor vacío para Jackson
    public SincronizarClienteRequest() {}
}