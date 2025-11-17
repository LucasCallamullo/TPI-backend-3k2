package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "camiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Camion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String dominio;

    private String nombreConductor;
    private String telefonoConductor;

    @Builder.Default
    private Boolean disponible = true;
    
    private Double costoPorKm;
    private Double consumoCombustibleLx100km;

    private String modelo;
    private Double capacidadPesoKg;
    private Double capacidadVolumenM3;
}