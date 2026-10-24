package com.redpatrimonial.ms_usuarios.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PerfilGuiaDTO {

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    private String idiomas;

    private String biografia;
}
