package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_contenedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoContenedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String nombre; // "DISPONIBLE", "ASIGNADO", "EN_TRANSITO", "ENTREGADO", "EN_DEPOSITO"
}