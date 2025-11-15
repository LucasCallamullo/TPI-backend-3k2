package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
@Table(name = "camiones")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dominio;
    private String modelo;
    private Double capacidadPesoKg;
    private Double capacidadVolumenM3;
    private Boolean disponible;
    private Double costoPorKm;
    private Double consumoCombustibleLx100km;
    private String nombreConductor;
    private String telefonoConductor;

}
