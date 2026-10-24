package com.redpatrimonial.ms_rutas.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

// Salida pedagógica (CU-12) con solicitud de guía opcional (CU-13)
@Data
public class SalidaDTO {

    @NotNull(message = "El docente es obligatorio")
    private Long idDocente;

    @NotNull(message = "La ruta es obligatoria")
    private Long idRuta;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede estar en el pasado")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "El tamaño del grupo es obligatorio")
    @Min(value = 1, message = "El grupo debe tener al menos 1 estudiante")
    @Max(value = 60, message = "El grupo no puede superar 60 estudiantes")
    private Integer tamanoGrupo;

    @NotBlank(message = "El nivel educativo es obligatorio")
    private String nivelEducativo;

    private String observaciones;

    private boolean solicitarGuia;

    // Obligatorio si solicitarGuia es true
    private Long idRecintoGuia;

    private String comentario;
}
