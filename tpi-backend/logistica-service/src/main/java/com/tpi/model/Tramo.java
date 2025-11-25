package com.tpi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
@Table(name = "tramos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;
    
    @ManyToOne
    @JoinColumn(name = "origen_id")
    private Ubicacion origen;
    
    @ManyToOne
    @JoinColumn(name = "destino_id")
    private Ubicacion destino;

    @ManyToOne
    @JoinColumn(name = "camion_id")
    private Camion camion;
    
    @ManyToOne
    @JoinColumn(name = "tipo_tramo_id")
    private TipoTramo tipo; // ORIGEN_DEPOSITO, DEPOSITO_DEPOSITO, DEPOSITO_DESTINO, ORIGEN_DESTINO
    
    @ManyToOne
    @JoinColumn(name = "estado_tramo_id")
    private EstadoTramo estado; // ESTIMADO, ASIGNADO, INICIADO, FINALIZADO
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHoraCreacion;    // se agrega el campo previo a persistir la entidad

    private Date fechaHoraInicio;

    // datos que sirven para el calculo de estadia en depositos
    private Date fechaHoraLlegada;    // llega  a deposito
    private Date fechaHoraFin;        // finaliza o reanuda saliedo de deposito
    
    // Calculado con OSRM local
    private Double distanciaKm;         
    private Long duracionEstimadaSegundos;    

    // Sirve para saber que tramo sería si el primero, segundo, etc
    // Empieza a contar desde 0
    private Integer orden;

    // Otro endpoint se encarga de hacer esto
    private Double costoAproximado;
    private Double costoReal;

    // Estadía en depósito (si aplica)
    private Integer diasEstadia;
    private Double costoEstadia;

    // ===== MÉTODOS DE CÁLCULO AUTOMÁTICO =====

    /**
     * Calcula la cantidad de días de estadía en depósito basado en las fechas reales.
     * 
     * @return Número de días de estadía (redondeado hacia arriba)
     * @example 
     * - Llegada: 2024-01-01 10:00, Salida: 2024-01-03 14:00 → 3 días
     * - Llegada: 2024-01-01 10:00, Salida: 2024-01-01 18:00 → 1 día
     */
    public Integer calcularDiasEstadia() {
        // Verificar que tenemos ambas fechas necesarias para el cálculo
        if (fechaHoraLlegada != null && fechaHoraFin != null) {
            
            // Calcular la diferencia en horas entre llegada y salida
            long horasEstadia = ChronoUnit.HOURS.between(
                fechaHoraLlegada.toInstant(),     // Convertir Date a Instant para cálculo
                fechaHoraFin.toInstant()          // Convertir Date a Instant para cálculo
            );
            
            // Convertir horas a días, redondeando hacia arriba
            // Ejemplo: 26 horas → 26/24 = 1.08 → ceil = 2 días
            return (int) Math.ceil(horasEstadia / 24.0);
        }
        
        // Si faltan fechas, no hay estadía calculable
        return 0;
    }
    
    /**
     * Calcula el costo de estadía basado en el depósito y las fechas reales.
     * Actualiza los campos diasEstadia y costoEstadia del tramo.
     * 
     * @param deposito El depósito donde se realizó la estadía
     * @return Costo total de la estadía en pesos
     * @example 
     * - 2 días × $2.500/día = $5.000
     * - 0 días (sin estadía) = $0
     */
    public Double calcularCostoEstadia(Deposito deposito) {
        // Verificar que tenemos un depósito válido
        if (deposito != null) {
            // 1. Calcular días de estadía basado en fechas reales
            this.diasEstadia = calcularDiasEstadia();
            
            // 2. Calcular costo total: días × costo diario del depósito
            this.costoEstadia = diasEstadia * deposito.getCostoEstadiaPorDia();
            
            // 3. Devolver el costo calculado
            return this.costoEstadia;
        }
        
        // Si no hay depósito, no hay costo de estadía
        return 0.0;
    }
    
    /**
     * Verifica si este tramo involucra estadía en depósito.
     * 
     * @return true si el destino es un depósito, false en caso contrario
     */
    public boolean involucraEstadiaEnDeposito() {
        return tipo != null && 
               (tipo.getNombre().contains("DEPOSITO") || 
                tipo.getNombre().equals("ORIGEN_DEPOSITO") ||
                tipo.getNombre().equals("DEPOSITO_DEPOSITO"));
    }
    
    /**
     * Calcula la duración real del tramo en horas.
     * 
     * @return Duración en horas, o null si faltan fechas
     */
    public Long calcularDuracionRealHoras() {
        if (fechaHoraInicio != null && fechaHoraFin != null) {
            return ChronoUnit.HOURS.between(
                fechaHoraInicio.toInstant(),
                fechaHoraFin.toInstant()
            );
        }
        return null;
    }

    
    @PrePersist
    protected void onCreate() {
        fechaHoraCreacion = new Date();  // Fecha actual al persistir
    }
}