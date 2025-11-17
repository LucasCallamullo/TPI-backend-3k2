package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
@Table(name = "deposito")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double costoEstadiaPorDia;

    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;
}
