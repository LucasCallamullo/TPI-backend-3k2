package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
@Table(name = "estado_tramo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoTramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

}
