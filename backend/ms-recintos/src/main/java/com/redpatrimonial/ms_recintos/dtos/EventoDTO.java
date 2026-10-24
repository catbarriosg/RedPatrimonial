package com.redpatrimonial.ms_recintos.dtos;

import com.redpatrimonial.ms_recintos.enums.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventoDTO {

    @NotNull(message = "El recinto es obligatorio")
    private Long idRecinto;

    private Long idAutor;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 120, message = "El título no puede superar 120 caracteres")
    private String titulo;

    private String descripcion;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;
}
