package com.redpatrimonial.ms_usuarios.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistroUsuarioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre no puede superar 60 caracteres")
    private String nombre;

    @Size(max = 60, message = "El apellido no puede superar 60 caracteres")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 60, message = "La contraseña debe tener entre 4 y 60 caracteres")
    private String password;

    @Pattern(regexp = "^$|^\\+?[0-9 ]{8,15}$", message = "El teléfono solo puede tener números, espacios y un + inicial")
    private String telefono;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(DOCENTE|ENCARGADO|GUIA|ADMIN)$", message = "El rol debe ser: DOCENTE, ENCARGADO, GUIA o ADMIN")
    private String rol;
}
