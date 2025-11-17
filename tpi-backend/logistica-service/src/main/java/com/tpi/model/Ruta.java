package com.tpi.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "rutas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long solicitudId; // ID del ms-solicitudes

    @ManyToOne
    @JoinColumn(name = "tarifa_id")
    private Tarifa tarifa; // TARIFA CONGELADA para toda la ruta

    // Estos campos SÍ son útiles para consultas rápidas (denormalización)
    private Integer cantidadTramos;
    private Integer cantidadDepositos;

    // Cache de cálculos (opcional pero útil)
    private Double costoEstimado;
    private Double costoFinal;

    // =====
    // Todos estos datos no hacen falta aca ya estarían en los tramos parcialmente
    // o en la solicitud los totales, ruta hace de clase contendora nomas

    // private Double costoEstimado;
    // private Double tiempoEstimado; // en horas

    // private Double costoFinal;
    // private Double tiempoReal; // en horas

    // RELACIÓN CON TRAMOS (NECESARIA)
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Tramo> tramos = new ArrayList<>();
}
