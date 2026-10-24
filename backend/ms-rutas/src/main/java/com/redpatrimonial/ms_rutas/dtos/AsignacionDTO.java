package com.redpatrimonial.ms_rutas.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// CU-20: el encargado asigna un guía a una solicitud
@Data
public class AsignacionDTO {

    @NotNull(message = "La solicitud es obligatoria")
    private Long idSolicitud;

    @NotNull(message = "El guía es obligatorio")
    private Long idGuia;

    private Long idEncargado;
}
