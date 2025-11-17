package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "ubicacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String direccion;
    private String nombre;
    private Double latitud;
    private Double longitud;

    @ManyToOne
    @JoinColumn(name = "tipo_ubicacion_id")    // DEPOSITO ORIDEN DESTINO CLIENTE
    private TipoUbicacion tipo;
}
