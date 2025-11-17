package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;
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
    
    private Double costoEstimado;
    private Double tiempoEstimado;     // en horas
    
    private Double costoFinal;
    private Double tiempoReal;     // en horas
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoSolicitud estado;    // "BORRADOR", "PROGRAMADA", "EN_TRANSITO", "ENTREGADA"
    
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}