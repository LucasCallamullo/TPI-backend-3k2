package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
@Table(name = "tipo_ubicacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoUbicacion {    // DEPOSITO ORIDEN DESTINO CLIENTE

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;
}
