package com.redpatrimonial.ms_usuarios.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PerfilDocenteDTO {

    @NotBlank(message = "El establecimiento es obligatorio")
    private String establecimiento;

    private String asignatura;
}
