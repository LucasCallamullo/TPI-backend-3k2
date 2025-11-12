package com.tpi.gateway.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
public class AuthController {

    // Constantes para URLs y configuración - FÁCIL DE MANTENER
    private static final String KEYCLOAK_TOKEN_URL = "http://localhost:8081/realms/tpi-backend/protocol/openid-connect/token";
    private static final String CLIENTES_SYNC_URL = "http://localhost:8082/api/v1/clientes/sincronizar";
    private static final String REDIRECT_URI = "http://localhost:8080/api/login/oauth2/code/keycloak";
    private static final String CLIENT_ID = "tpi-backend-client";

    // ObjectMapper como constante para reutilización
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Endpoint callback de OAuth2 que maneja el flujo de autenticación con Keycloak.
     * Recibe el código de autorización, lo intercambia por un token, y sincroniza el usuario.
     * 
     * @param code Código de autorización proporcionado por Keycloak
     * @return Mensaje de confirmación del login
     * @throws Exception Si ocurre error en el proceso de autenticación
     */
    @SuppressWarnings("null")
    @GetMapping("/api/login/oauth2/code/keycloak")
    public String intercambiarCode(@RequestParam String code) throws Exception {
        // Crear cliente REST para hacer peticiones HTTP
        RestClient restClient = RestClient.create();

        // PASO 1: Construir los parámetros para el request del token
        String formData = "grant_type=authorization_code" +
            "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
            "&client_id=" + CLIENT_ID +
            "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        // PASO 2: Intercambiar el código por un token de acceso en Keycloak
        String tokenResponse = restClient.post()
            .uri(KEYCLOAK_TOKEN_URL)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formData)
            .retrieve()
            .body(String.class);

        // PASO 3: Parsear la respuesta JSON del token
        JsonNode tokenJson = OBJECT_MAPPER.readTree(tokenResponse);
        String accessToken = tokenJson.get("access_token").asText();

        // PASO 4: Decodificar el JWT para extraer información del usuario
        // Un JWT tiene 3 partes: header.payload.signature, separadas por puntos
        String[] chunks = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        
        // Decodificar el payload (segunda parte del JWT)
        String payload = new String(decoder.decode(chunks[1]));
        JsonNode payloadJson = OBJECT_MAPPER.readTree(payload);
        
        // Extraer datos del usuario del payload del JWT
        String keycloakId = payloadJson.get("sub").asText();          // ID único del usuario en Keycloak
        String email = payloadJson.get("email").asText();             // Email del usuario
        String nombre = payloadJson.get("preferred_username").asText(); // Nombre de usuario preferido

        // PASO 5: Sincronizar usuario con el microservicio de clientes
        // Preparar los datos del cliente para sincronizar
        Map<String, String> syncRequest = Map.of(
            "keycloakId", keycloakId,
            "nombre", nombre,
            "email", email
        );

        try {
            // Enviar request al microservicio de clientes con el token en el header
            restClient.post()
                .uri(CLIENTES_SYNC_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken) // Incluir token para autorización
                .body(syncRequest)
                .retrieve()
                .toBodilessEntity(); // No necesitamos el cuerpo de la respuesta
            
            System.out.println("✅ Cliente sincronizado exitosamente: " + email);
            
        } catch (Exception e) {
            // Log del error pero continuamos para no interrumpir el flujo de login
            System.err.println("❌ Error sincronizando cliente: " + e.getMessage());
            // En un entorno productivo, usaríamos un logger como SLF4J
        }

        // Respuesta HTML para el usuario
        return "✅ Login exitoso! Cliente sincronizado.<br>" +
            "Keycloak ID: " + keycloakId + "<br>" +
            "Email: " + email + "<br>" +
            "<a href='/'>Volver al inicio</a>";
    }
}

/*
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/api/login/oauth2/code/keycloak")
    public String intercambiarCode(@RequestParam String code) throws Exception {
        RestClient restClient = RestClient.create();

        String formData = "grant_type=authorization_code" +
            "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
            "&client_id=tpi-backend-client" +
            "&redirect_uri=" + URLEncoder.encode("http://localhost:8080/api/login/oauth2/code/keycloak", StandardCharsets.UTF_8);

        // 1. Obtener token de Keycloak
        String tokenResponse = restClient.post()
            .uri("http://localhost:8081/realms/tpi-backend/protocol/openid-connect/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formData)
            .retrieve()
            .body(String.class);

        // 2. Parsear el token response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tokenJson = mapper.readTree(tokenResponse);
        String accessToken = tokenJson.get("access_token").asText();

        // 3. Decodificar el JWT
        String[] chunks = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        
        JsonNode payloadJson = mapper.readTree(payload);
        String keycloakId = payloadJson.get("sub").asText();
        String email = payloadJson.get("email").asText();
        String nombre = payloadJson.get("preferred_username").asText();

        // 4. Sincronizar con ms-clientes - INCLUIR EL TOKEN EN EL HEADER
        Map<String, String> syncRequest = Map.of(
            "keycloakId", keycloakId,
            "nombre", nombre,
            "email", email
        );

        try {
            restClient.post()
                .uri("http://localhost:8082/api/v1/clientes/sincronizar")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(syncRequest)
                .retrieve()
                .toBodilessEntity();
            
            System.out.println("✅ Cliente sincronizado exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error sincronizando cliente: " + e.getMessage());
            // Continúa aunque falle la sincronización
        }

        return "✅ Login exitoso! Cliente sincronizado.<br>" +
            "Keycloak ID: " + keycloakId + "<br>" +
            "<a href='/'>Volver al inicio</a>";
    }
}


/* Legacy este andaba antes
*/