package com.tpi.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public record ClienteRequest(

    @NotBlank
    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez")
    String nombre,

    @NotBlank
    @Schema(description = "Correo electrónico del cliente", example = "juan.perez@mail.com")
    String email,

    @NotBlank
    @Schema(description = "Contraseña para el usuario", example = "Secreta123!")
    String password,

    @Schema(description = "Número de teléfono del cliente", example = "+54 911 1234 5678")
    String telefono,

    @Schema(description = "Dirección del cliente", example = "Calle Falsa 123, Ciudad")
    String direccion
) {}

