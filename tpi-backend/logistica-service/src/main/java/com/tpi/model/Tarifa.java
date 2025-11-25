package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tarifas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // "Tarifa Pequeño - 0-20m³" --- "Tarifa Mediano - 20-40m³"
    // "Tarifa Grande - 40-70m³" --- "Tarifa Extra Grande - 70+m³"
    private String nombre; // Identificador descriptivo: "Tarifa Estándar 2025"
    private String descripcion; 

    private Double volumenMin; // Ej: 1.0 base, 1.2 para contenedores pesados
    private Double volumenMax; // Ej: 1.0 base, 1.2 para contenedores pesados
    
    // Costos fijos
    private Double costoGestionPorTramo;
    
    // Costos variables base (para cálculos estimados)
    private Double precioCombustiblePorLitro;        // VALOR FIJO DE CADA TARIFA ?
}