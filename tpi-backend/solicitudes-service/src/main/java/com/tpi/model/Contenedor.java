package com.tpi.model;

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
    
    private Double peso;
    private Double volumen;
    
    // Identificación única física del contenedor
    @Column(unique = true)
    private String identificacionUnica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_contenedor_id")
    private EstadoContenedor estado;
    
    // NO tiene relación directa con Cliente aquí
    // La relación es a través de Solicitud
}