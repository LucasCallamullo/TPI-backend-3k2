package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un Cliente en el sistema.
 * 
 * Esta clase mapea la tabla "clientes" en la base de datos y almacena
 * la información de negocio de los clientes, complementando la información
 * de autenticación gestionada por Keycloak.
 * 
 * El ID del cliente corresponde al "sub" (subject) del token JWT de Keycloak,
 * lo que permite mantener la consistencia entre la identidad en Keycloak
 * y los datos de negocio en esta base de datos.
 */
@Entity
@Table(name = "clientes")
@Data                    // Genera getters, setters, equals, hashCode y toString
@NoArgsConstructor       // Genera constructor sin argumentos
@AllArgsConstructor      // Genera constructor con todos los argumentos
@Builder                 // Permite usar el patrón Builder para crear instancias
public class Cliente {

    /**
     * Identificador único del cliente.
     * Corresponde al "sub" (subject) del token JWT de Keycloak.
     * Este campo sirve como clave primaria y como enlace con el usuario en Keycloak.
     */
    @Id
    private String id;

    /**
     * Nombre completo del cliente.
     * Campo obligatorio según las reglas de negocio.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Dirección de correo electrónico del cliente.
     * Campo obligatorio para comunicación y notificaciones.
     */
    @Column(nullable = false)
    private String email;

    /**
     * Número de teléfono de contacto del cliente.
     * Campo opcional para comunicación adicional.
     */
    private String telefono;

    /**
     * Dirección física del cliente.
     * Campo opcional para información de ubicación.
     */
    private String direccion;
    
    /**
     * Fecha y hora en que se creó el registro del cliente.
     * Campo de auditoría que se establece automáticamente al crear el registro.
     * No se actualiza en operaciones de modificación.
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    /**
     * Método callback de JPA que se ejecuta antes de persistir la entidad.
     * Establece automáticamente la fecha de creación con la fecha y hora actuales.
     * Esto garantiza que todos los registros tengan una fecha de creación consistente.
     */
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}

/* 
@Entity
@Table(name = "clientes")
@Data                   
@NoArgsConstructor      
@AllArgsConstructor      
@Builder         
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
}
*/