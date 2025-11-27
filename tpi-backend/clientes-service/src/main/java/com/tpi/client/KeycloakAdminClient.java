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

    /**
     * Crea un usuario en Keycloak utilizando la API de administración.
     *
     * @param user Datos del usuario a crear en Keycloak.
     * @return ID del usuario creado en Keycloak.
     * @throws RuntimeException si ocurre cualquier error durante la creación.
     */
    @SuppressWarnings("null")
    public String createUser(KeycloakUserDto user) {
        log.info("Creando usuario en Keycloak: {}", user.username());

        // 1. Obtener token de administrador necesario para llamar a la API REST de Keycloak
        String token = obtenerTokenAdmin();

        // Declarar headers HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construir petición con headers + body
        HttpEntity<KeycloakUserDto> entity = new HttpEntity<>(user, headers);

        // Endpoint de creación de usuario
        String url = keycloakUrl + "/admin/realms/" + realm + "/users";

        try {
            // 2. Ejecutar POST hacia Keycloak
            ResponseEntity<Void> response = keycloackRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );

            // Información útil para debugging en caso de fallas
            log.info("📥 Response Headers: {}", response.getHeaders());
            log.info("📍 Location Header: {}", response.getHeaders().getLocation());

            // 3. Validación del código de estado
            if (response.getStatusCode() != HttpStatus.CREATED) {
                log.error("Error creando usuario. Status: {}", response.getStatusCode());
                throw new RuntimeException("Error creando usuario en Keycloak: " + response.getStatusCode());
            }

            // 4. INTENTO 1 → Obtener ID desde Location (ideal)
            if (response.getHeaders().getLocation() != null) {
                String location = response.getHeaders().getLocation().toString();
                String userIdFromLocation = location.substring(location.lastIndexOf("/") + 1);

                log.info("✅ ID obtenido desde Location: {}", userIdFromLocation);
                return userIdFromLocation;
            }

            // 5. INTENTO 2 → Si Keycloak NO devuelve Location (suele pasar), buscar por email
            log.info("🔄 Location header no disponible, usando búsqueda...");

            String userId = findUserIdWithRetry(user.email(), token);

            log.info("✅ Usuario creado exitosamente con ID: {}", userId);
            return userId;

        } catch (Exception e) {
            log.error("Error en createUser para email: {}", user.email(), e);
            throw new RuntimeException("Error creando usuario: " + e.getMessage(), e);
        }
    }


    /**
     * Busca el ID de un usuario recientemente creado usando reintentos.
     * 
     * Dado que Keycloak a veces no devuelve el ID en el header "Location",
     * este método realiza una búsqueda por email hasta que el usuario aparezca.
     *
     * @param email Email del usuario recién creado.
     * @param token Token de administrador para autenticar la petición.
     * @return ID del usuario encontrado en Keycloak.
     * @throws RuntimeException si no se logra encontrar el usuario tras varios intentos.
     */
    private String findUserIdWithRetry(String email, String token) {
        int maxRetries = 5;
        int retryDelayMs = 500;

        for (int i = 0; i < maxRetries; i++) {
            try {
                // Intentar obtener el usuario por email
                String userId = findUserIdByEmail(email, token);
                if (userId != null) {
                    return userId;
                }
            } catch (Exception e) {
                log.warn("Intento {} fallado al buscar usuario: {}", i + 1, e.getMessage());
            }

            // Esperar antes de reintentar
            try {
                Thread.sleep(retryDelayMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Búsqueda interrumpida", ie);
            }
        }

        throw new RuntimeException("No se pudo encontrar el usuario después de " + maxRetries + " intentos");
    }


    /**
     * Busca un usuario en Keycloak por su email y devuelve su ID si existe.
     *
     * Este método realiza una consulta al endpoint de administración de Keycloak
     * filtrando por email. Si encuentra el usuario en la respuesta, devuelve su ID.
     * Si no se encuentra, devuelve null.
     *
     * @param email Email del usuario a buscar.
     * @param token Token de administrador para autenticar la petición.
     * @return ID del usuario si existe, o null si no fue encontrado.
     * @throws RuntimeException si ocurre un error al realizar la búsqueda.
     */
    @SuppressWarnings({ "null", "rawtypes" })
    private String findUserIdByEmail(String email, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            // Encodear email para evitar errores con caracteres especiales
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            String searchUrl = keycloakUrl + "/admin/realms/" + realm + "/users?email=" + encodedEmail;
            
            log.debug("Buscando usuario con URL: {}", searchUrl);

            ResponseEntity<Map[]> response = keycloackRestTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map[].class
            );

            // Si la respuesta contiene al menos un usuario, devolver su ID
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


    /**
     * Obtiene un token de administrador desde Keycloak utilizando el flujo
     * "password grant". Este token permite realizar operaciones administrativas
     * como creación de usuarios, actualización de roles, etc.
     *
     * @return Token JWT de administrador obtenido desde Keycloak.
     */
    @SuppressWarnings("null")
    private String obtenerTokenAdmin() {

        // Log informativo indicando el inicio del proceso
        log.info("Obteniendo token de admin para Keycloak");

        // Construcción de la URL del endpoint para obtener el token del realm "master"
        String url = keycloakUrl + "/realms/master/protocol/openid-connect/token";
        log.debug("URL de token: {}", url);

        // Configuración de headers para enviar datos como formulario application/x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Construcción del cuerpo del formulario con los parámetros necesarios
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password"); // Tipo de flujo OAuth2
        form.add("client_id", "admin-cli"); // Cliente interno de administración de Keycloak
        form.add("username", adminUser);    // Usuario administrador
        form.add("password", adminPass);    // Contraseña del administrador

        // Se crea la entidad HTTP combinando headers y cuerpo del formulario
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        // Se realiza la llamada POST al endpoint de Keycloak para obtener el token
        KeycloakTokenResponse tokenResponse = keycloackRestTemplate
                .postForObject(url, entity, KeycloakTokenResponse.class);

        // Log del objeto recibido para debug
        log.debug("TokenResponse: {}", tokenResponse);

        // Se retorna únicamente el access_token extraído del response
        return tokenResponse.access_token();
    }


    @SuppressWarnings({ "null", "rawtypes" })
    public void assignRole(String userId, String roleName) {

        // Log inicial indicando la asignación del rol
        log.info("Asignando rol '{}' al usuario '{}'", roleName, userId);

        // 1. Obtener token de administrador para poder realizar operaciones privilegiadas
        String token = obtenerTokenAdmin();

        // 2. Preparar headers con autenticación Bearer + JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);                 // Token admin
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ============================================================
        // 3. OBTENER EL OBJETO COMPLETO DEL ROL DESDE KEYCLOAK
        // ============================================================

        // Construcción de la URL para obtener información del rol
        String roleUrl = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;
        log.debug("URL para obtener rol: {}", roleUrl);

        // Se arma la entidad HTTP con headers (sin body)
        HttpEntity<Void> roleEntity = new HttpEntity<>(headers);

        // Petición GET para obtener los datos del rol
        ResponseEntity<Map> roleResponse = keycloackRestTemplate.exchange(
                roleUrl,
                HttpMethod.GET,
                roleEntity,
                Map.class
        );

        // Log de debug con la información del rol
        log.debug("Rol obtenido: {}", roleResponse.getBody());

        // ============================================================
        // 4. ASIGNAR EL ROL AL USUARIO
        // ============================================================

        // El endpoint requiere una LISTA con el rol como objeto JSON
        HttpEntity<Object> entity = new HttpEntity<>(List.of(roleResponse.getBody()), headers);

        // Construcción de la URL para asignar el rol en los mappings de realm
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        log.debug("URL para asignar rol: {}", url);

        // Se envía el POST sin esperar respuesta de contenido
        keycloackRestTemplate.postForEntity(url, entity, Void.class);

        // Log final confirmando la operación
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
