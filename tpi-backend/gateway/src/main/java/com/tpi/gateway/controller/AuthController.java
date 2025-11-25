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

    // ================================
    // URLS PARA DOCKER
    // ================================
    // URLs INTERNAS (misma red Docker)
    private static final String KEYCLOAK_TOKEN_URL = "http://keycloak:8080/realms/tpi-backend/protocol/openid-connect/token";
    private static final String CLIENTES_SYNC_URL = "http://clientes-service:8082/api/v1/clientes/sincronizar";
    private static final String REDIRECT_URI = "http://localhost:8080/api/login/oauth2/code/keycloak";


    // OPCIÓN B: TODO LOCALHOST (Para desarrollo)
    // private static final String KEYCLOAK_TOKEN_URL = "http://localhost:8081/realms/tpi-backend/protocol/openid-connect/token";
    // private static final String CLIENTES_SYNC_URL = "http://localhost:8082/api/v1/clientes/sincronizar";
    // private static final String REDIRECT_URI = "http://localhost:8080/api/login/oauth2/code/keycloak";

    private static final String CLIENT_ID = "tpi-backend-client";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // =============================================
    //  CALLBACK DE KEYCLOAK (AUTENTICACIÓN)
    // =============================================
    @SuppressWarnings("null")
    @GetMapping("/api/login/oauth2/code/keycloak")
    public String intercambiarCode(@RequestParam String code) throws Exception {

        RestClient restClient = RestClient.create();

        System.out.println("===============================================");
        System.out.println("🔔 CALLBACK DE KEYCLOAK RECIBIDO");
        System.out.println("🔹 Authorization Code: " + code);
        System.out.println("===============================================");

        // -------------------------------
        // 1 Construir Form Data
        // -------------------------------
        String formData = "grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                "&client_id=" + CLIENT_ID +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        System.out.println("📨 Enviando request a Keycloak TOKEN URL:");
        System.out.println("➡ " + KEYCLOAK_TOKEN_URL);
        System.out.println("➡ FORM DATA = " + formData);

        // -------------------------------
        // 2 Intercambiar CODE por TOKEN
        // -------------------------------
        String tokenResponse;
        try {
            tokenResponse = restClient.post()
                    .uri(KEYCLOAK_TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            System.err.println("❌ ERROR al pedir token a Keycloak: " + e.getMessage());
            throw new RuntimeException("Falló el intercambio code/token con Keycloak");
        }

        System.out.println("🔓 TOKEN RESPONSE RECIBIDO:");
        System.out.println("➡ " + tokenResponse);

        // -------------------------------
        // 3 Parsear JSON del token
        // -------------------------------
        JsonNode tokenJson = OBJECT_MAPPER.readTree(tokenResponse);
        String accessToken = tokenJson.get("access_token").asText();

        System.out.println("🔑 ACCESS TOKEN (primeros 20 chars): " +
                accessToken.substring(0, 20) + "...");

        // -------------------------------
        // 4 Decodificar JWT
        // -------------------------------
        String[] chunks = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payloadJsonStr = new String(decoder.decode(chunks[1]));

        System.out.println("🧩 JWT PAYLOAD:");
        System.out.println(payloadJsonStr);

        JsonNode payload = OBJECT_MAPPER.readTree(payloadJsonStr);

        String keycloakId   = payload.get("sub").asText();
        String email        = payload.has("email") ? payload.get("email").asText() : "sin-email";
        String nombre       = payload.has("preferred_username") ?
                              payload.get("preferred_username").asText() : "sin-username";

        System.out.println("👤 DATOS DEL USUARIO:");
        System.out.println("➡ Keycloak ID = " + keycloakId);
        System.out.println("➡ Email       = " + email);
        System.out.println("➡ Username    = " + nombre);

        // -------------------------------
        // 5 Sincronizar con clientes-service
        // -------------------------------
        Map<String, String> syncRequest = Map.of(
                "keycloakId", keycloakId,
                "nombre", nombre,
                "email", email
        );

        System.out.println("🔁 SINCRONIZANDO CLIENTE EN clientes-service...");
        System.out.println("➡ URL: " + CLIENTES_SYNC_URL);
        System.out.println("➡ Body: " + syncRequest);

        try {
            restClient.post()
                    .uri(CLIENTES_SYNC_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(syncRequest)
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("✅ Cliente sincronizado correctamente");
        } catch (Exception e) {
            System.err.println("❌ ERROR sincronizando cliente:");
            e.printStackTrace();
        }

        System.out.println("🏁 FLUJO DE LOGIN COMPLETADO EXITOSAMENTE");
        System.out.println("===============================================");

        return """
                <h2>Login exitoso! Usuario sincronizado.</h2>
                <p><b>Keycloak ID:</b> """ + keycloakId + "</p>" +
                "<p><b>Email:</b> " + email + "</p>" +
                "<a href='/'>Volver al inicio</a>";
    }
}
