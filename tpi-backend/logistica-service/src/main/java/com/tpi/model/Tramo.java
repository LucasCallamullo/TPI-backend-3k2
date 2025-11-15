package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Data
@Entity
@Table(name = "tramo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rutaid;
    private Long origenid;
    private Long destinoid;
    private int tipoid;
    private int estadoid;
    private Date fechaInicio;
    private Date fechaFin;
    private Long caminoId;
    private double costoAproximado;
    private double costoReal;
    private int tarifaId;
    
}
