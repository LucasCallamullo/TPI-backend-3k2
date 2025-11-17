package com.tpi.dto;

import lombok.*;

// En ms-logistica - NO es una Entity!
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorDTO {
    private Long id;
    private Double peso;
    private Double volumen;
    private String identificacionUnica;
}