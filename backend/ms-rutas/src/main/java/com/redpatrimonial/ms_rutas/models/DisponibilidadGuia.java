package com.redpatrimonial.ms_rutas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

// Bloque semanal en que un guía puede acompañar recorridos. diaSemana: 1 = lunes ... 7 = domingo
@Data
@Entity
@Table(name = "disponibilidad_guia")
public class DisponibilidadGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idGuia;

    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 1, message = "El día debe estar entre 1 (lunes) y 7 (domingo)")
    @Max(value = 7, message = "El día debe estar entre 1 (lunes) y 7 (domingo)")
    private Integer diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de término es obligatoria")
    private LocalTime horaFin;
}
