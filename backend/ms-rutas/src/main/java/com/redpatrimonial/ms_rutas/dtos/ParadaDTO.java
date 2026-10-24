package com.redpatrimonial.ms_rutas.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParadaDTO {

    @NotNull(message = "El recinto de la parada es obligatorio")
    private Long idRecinto;

    @Min(value = 0, message = "El traslado no puede ser negativo")
    private Integer trasladoDesdeAnteriorMin = 0;

    @Min(value = 5, message = "La visita debe durar al menos 5 minutos")
    private Integer tiempoVisitaMin = 20;
}
