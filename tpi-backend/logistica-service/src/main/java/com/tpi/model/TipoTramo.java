package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
@Table(name = "tipo_tramo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoTramo {    // tipo(origen-deposito, deposito-deposito, deposito-destino, origen-destino)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;
}