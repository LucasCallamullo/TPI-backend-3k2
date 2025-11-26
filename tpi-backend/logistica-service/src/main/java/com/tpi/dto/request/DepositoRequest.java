package com.tpi.dto.request;


/*
Ejemplo
{
  "nombre": "Depósito Central",
  "costoEstadiaPorDia": 2500.0,
  "ubicacion": {
    "direccion": "San Martín 2020, Santa Fe, Argentina",
    "nombre": "Almacén Santa Fe Norte",
    "latitud": -31.633329,
    "longitud": -60.700438,
    "tipo": 2
  }
}

*/

public record DepositoRequest(
    String nombre,
    Double costoEstadiaPorDia,
    UbicacionRequestDTO ubicacion
) {}

