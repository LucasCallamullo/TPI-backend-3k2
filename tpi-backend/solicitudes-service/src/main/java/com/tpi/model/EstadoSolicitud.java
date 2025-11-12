package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoSolicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String nombre; // "BORRADOR", "PROGRAMADA", "EN_TRANSITO", "ENTREGADA"
}