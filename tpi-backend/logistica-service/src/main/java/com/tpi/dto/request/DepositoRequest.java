package com.tpi.dto.request;


/*
Ejemplo
{
  "nombre": "Depósito Central",
  "costoEstadiaPorDia": 2500.0,
  "ubicacion": {
    "direccion": "Av. Siempre Viva 123",
    "nombre": "Almacén 1",
    "latitud": -34.603722,
    "longitud": -58.381592,
    "tipoId": 2
  }
}
*/

public record DepositoRequest(
    String nombre,
    Double costoEstadiaPorDia,
    UbicacionRequestDTO ubicacion
) {}

