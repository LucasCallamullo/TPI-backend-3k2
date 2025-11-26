package com.tpi.client;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;

import lombok.Getter;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class KeycloakAdminClient {

    private String keycloakUrl = "http://192.168.100.124:8081";
    private String realm = "tpi-backend";
    private String clientId = "tpi-backend-client";
    private String clientSecret = ""; // si no tenés client-secret, dejalo vacío
    private String adminUser = "admin";
    private String adminPass = "admin123";

    private final RestTemplate keycloackRestTemplate;

    @SuppressWarnings("null")
    public String createUser(KeycloakUserDto user) {
        log.info("Creando usuario en Keycloak: {}", user.username());

        String token = obtenerTokenAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<KeycloakUserDto> entity = new HttpEntity<>(user, headers);
        String url = keycloakUrl + "/admin/realms/" + realm + "/users";

        try {
            ResponseEntity<Void> response = keycloackRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );

            log.info("📥 Response Headers: {}", response.getHeaders());
            log.info("📍 Location Header: {}", response.getHeaders().getLocation());

            if (response.getStatusCode() != HttpStatus.CREATED) {
                log.error("Error creando usuario. Status: {}", response.getStatusCode());
                throw new RuntimeException("Error creando usuario en Keycloak: " + response.getStatusCode());
            }

            // INTENTO 1: Obtener ID desde Location header si está disponible
            if (response.getHeaders().getLocation() != null) {
                String location = response.getHeaders().getLocation().toString();
                String userIdFromLocation = location.substring(location.lastIndexOf("/") + 1);
                log.info("✅ ID obtenido desde Location: {}", userIdFromLocation);
                return userIdFromLocation;
            }

            // INTENTO 2: Búsqueda con retry
            log.info("🔄 Location header no disponible, usando búsqueda...");
            String userId = findUserIdWithRetry(user.email(), token);
            log.info("✅ Usuario creado exitosamente con ID: {}", userId);
            return userId;

        } catch (Exception e) {
            log.error("Error en createUser para email: {}", user.email(), e);
            throw new RuntimeException("Error creando usuario: " + e.getMessage(), e);
        }
    }

    private String findUserIdWithRetry(String email, String token) {
        int maxRetries = 5;
        int retryDelayMs = 500;

        for (int i = 0; i < maxRetries; i++) {
            try {
                String userId = findUserIdByEmail(email, token);
                if (userId != null) {
                    return userId;
                }
            } catch (Exception e) {
                log.warn("Intento {} fallado al buscar usuario: {}", i + 1, e.getMessage());
            }

            // Esperar antes del siguiente intento
            try {
                Thread.sleep(retryDelayMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Búsqueda interrumpida", ie);
            }
        }

        throw new RuntimeException("No se pudo encontrar el usuario después de " + maxRetries + " intentos");
    }

    @SuppressWarnings({ "null", "rawtypes" })
    private String findUserIdByEmail(String email, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            // ✅ ENCODEAR EL EMAIL PARA LA URL
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            String searchUrl = keycloakUrl + "/admin/realms/" + realm + "/users?email=" + encodedEmail;
            
            log.debug("Buscando usuario con URL: {}", searchUrl);

            ResponseEntity<Map[]> response = keycloackRestTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map[].class
            );

            if (response.getBody() != null && response.getBody().length > 0) {
                String userId = (String) response.getBody()[0].get("id");
                log.debug("Usuario encontrado con ID: {}", userId);
                return userId;
            }
            
            log.debug("Usuario no encontrado en búsqueda para email: {}", email);
            return null;
            
        } catch (Exception e) {
            log.error("Error buscando usuario por email: {}", email, e);
            throw new RuntimeException("Error buscando usuario: " + e.getMessage(), e);
        }
    }





    @SuppressWarnings("null")
    private String obtenerTokenAdmin() {
        log.info("Obteniendo token de admin para Keycloak");

        String url = keycloakUrl + "/realms/master/protocol/openid-connect/token";
        log.debug("URL de token: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", adminUser);
        form.add("password", adminPass);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        KeycloakTokenResponse tokenResponse = keycloackRestTemplate
                .postForObject(url, entity, KeycloakTokenResponse.class);

        log.debug("TokenResponse: {}", tokenResponse);

        return tokenResponse.access_token();
    }

    @SuppressWarnings({ "null", "rawtypes" })
    public void assignRole(String userId, String roleName) {
        log.info("Asignando rol '{}' al usuario '{}'", roleName, userId);

        String token = obtenerTokenAdmin();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Obtener los roles del realm (con headers correctamente)
        String roleUrl = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;
        log.debug("URL para obtener rol: {}", roleUrl);

        HttpEntity<Void> roleEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> roleResponse = keycloackRestTemplate.exchange(
                roleUrl,
                HttpMethod.GET,
                roleEntity,
                Map.class
        );

        log.debug("Rol obtenido: {}", roleResponse.getBody());

        // Asignar el rol
        HttpEntity<Object> entity = new HttpEntity<>(List.of(roleResponse.getBody()), headers);

        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        log.debug("URL para asignar rol: {}", url);

        keycloackRestTemplate.postForEntity(url, entity, Void.class);
        log.info("Rol '{}' asignado al usuario '{}'", roleName, userId);
    }


    /*
        Para crear el DTO a mandar
    */
    public record KeycloakUserDto(
        String username,
        String email,
        boolean enabled,
        List<Credential> credentials
    ) {
        public record Credential(
                String type,
                String value,
                boolean temporary
        ) {}
    }

    public record KeycloakTokenResponse(
        String access_token,
        String token_type,
        long expires_in,
        long refresh_expires_in,
        String refresh_token
    ) {}
}
