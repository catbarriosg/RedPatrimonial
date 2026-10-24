package com.redpatrimonial.ms_recintos.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

// Datos para registrar (CU-24) o actualizar (CU-16) un recinto
@Data
public class RecintoDTO {

    @NotBlank(message = "El nombre oficial es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombreOficial;

    @NotNull(message = "La comuna es obligatoria")
    private Long idComuna;

    @NotNull(message = "El tipo de recinto es obligatorio")
    private Long idTipo;

    private Long idEncargado;
    private Long idAdminRegistro;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitud;

    @NotBlank(message = "La reseña histórica es obligatoria")
    private String resenaHistorica;

    private String categoria;
    private String estilo;
    private String siglo;
}
