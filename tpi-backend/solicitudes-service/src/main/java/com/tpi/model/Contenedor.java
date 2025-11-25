package com.tpi.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contenedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contenedor {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String identificacionUnica; // Código físico del contenedor
    
    private Double peso; // en kg
    private Double volumen; // en m³
    private String descripcion;

    @Column(name = "cliente_id") // ← NULLABLE - puede no tener dueño
    private String clienteId; // Solo cuando está asignado a un cliente
    
    @ManyToOne(fetch = FetchType.EAGER)        // No lazy
    @JoinColumn(name = "estado_contenedor_id")
    private EstadoContenedor estado;
    
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    /* / Dimensiones estándar opcional, para referencia
    private Double largo; // en metros
    private Double ancho; // en metros  
    private Double alto;  // en metros */
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}