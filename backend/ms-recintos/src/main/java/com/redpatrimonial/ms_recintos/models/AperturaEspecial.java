package com.redpatrimonial.ms_recintos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

// Bloque horario en que un recinto cerrado abre de forma excepcional (CU-18)
@Getter
@Setter
@Entity
@Table(name = "aperturas_especiales")
public class AperturaEspecial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_recinto")
    private Recinto recinto;

    private Long idUsuario;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede estar en el pasado")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de término es obligatoria")
    private LocalTime horaFin;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @JsonProperty("idRecinto")
    public Long getIdRecinto() {
        return recinto != null ? recinto.getId() : null;
    }
}
