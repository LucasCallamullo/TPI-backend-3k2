package com.tpi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

// =============================================
// CONFIGURACIÓN DE TEST CON SEGURIDAD MOCKEADA
// =============================================
@SpringBootTest
class ClientesServiceApplicationTests {

    // =============================================
    // MOCK DEL JWT DECODER PARA EVITAR ERRORES EN TESTS
    // =============================================
    // Este mock evita que Spring intente crear un JwtDecoder real
    // que requiere conexión a Keycloak durante los tests
    @SuppressWarnings("removal")
    @MockBean
    private JwtDecoder jwtDecoder;

    // =============================================
    // TEST BÁSICO DE CARGA DE CONTEXTO
    // =============================================
    // Este test verifica que el contexto de Spring se carga correctamente
    // sin errores de configuración de seguridad
    @Test
    void contextLoads() {
        // El test pasa si el contexto de Spring se carga sin excepciones
        // gracias al @MockBean de JwtDecoder que evita la necesidad de
        // conexión real con Keycloak
    }
}