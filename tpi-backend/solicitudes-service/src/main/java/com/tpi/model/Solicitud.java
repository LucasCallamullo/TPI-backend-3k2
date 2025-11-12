package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Referencia al cliente (keycloakId del ms-clientes)
    @Column(name = "cliente_id", nullable = false)
    private String clienteId;
    
    // Relación con contenedor (dentro del mismo microservicio)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenedor_id")
    private Contenedor contenedor;

    // Solo guardamos IDs, no relaciones JPA
    @Column(name = "origen_id")
    private Long origenId;           // ID de ubicación en ms-logistica
    
    @Column(name = "destino_id")  
    private Long destinoId;          // ID de ubicación en ms-logistica
    
    private BigDecimal costoEstimado;
    private Integer tiempoEstimado; // en horas
    private BigDecimal costoFinal;
    private Integer tiempoReal; // en horas
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoSolicitud estado;
    
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}