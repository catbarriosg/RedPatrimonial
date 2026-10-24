package com.redpatrimonial.ms_usuarios.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

// Respuesta del login: lo que la app necesita para mostrar el panel según el rol
@Data
@AllArgsConstructor
public class SesionDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
}
