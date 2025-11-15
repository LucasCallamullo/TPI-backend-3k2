package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
@Table(name = "ruta")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long solicitudId;
    private int cantidadTramos;
    
}
