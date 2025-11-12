package com.tpi.dto;

import lombok.Data;

@Data
public class SincronizarClienteRequest {
    private String keycloakId;
    private String nombre;
    private String email;
    private String telefono;
    
    // Constructor vacío para Jackson
    public SincronizarClienteRequest() {}
}