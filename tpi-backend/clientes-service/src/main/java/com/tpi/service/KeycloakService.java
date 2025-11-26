package com.tpi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tpi.client.KeycloakAdminClient;
import com.tpi.client.KeycloakAdminClient.KeycloakUserDto;
import com.tpi.dto.request.ClienteRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakAdminClient keycloakAdminClient;

    public String registrarUsuarioEnKeycloak(ClienteRequest request) {

        // 1. Crear DTO compatible con Keycloak
        KeycloakUserDto user = new KeycloakUserDto(
            request.email(),     // username = email
            request.email(),
            true,
            List.of(new KeycloakUserDto.Credential("password", request.password(), false))
        );

        // 2. Llamar a la API Admin
        String keycloakId = keycloakAdminClient.createUser(user);

        // 3. Asignar rol "cliente"
        keycloakAdminClient.assignRole(keycloakId, "Cliente");

        return keycloakId;
    }
}
