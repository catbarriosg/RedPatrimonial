package com.redpatrimonial.ms_rutas.dtos;

import lombok.Data;

// CU-22: respuesta del guía a un recorrido asignado
@Data
public class RespuestaDTO {

    private boolean acepta;

    private String motivoRechazo;
}
