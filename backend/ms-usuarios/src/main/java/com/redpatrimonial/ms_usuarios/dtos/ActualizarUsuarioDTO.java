package com.redpatrimonial.ms_usuarios.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Datos que el usuario puede cambiar de su cuenta. El correo y el rol no se modifican aquí.
@Data
public class ActualizarUsuarioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre no puede superar 60 caracteres")
    private String nombre;

    @Size(max = 60, message = "El apellido no puede superar 60 caracteres")
    private String apellido;

    @Pattern(regexp = "^$|^\\+?[0-9 ]{8,15}$", message = "El teléfono solo puede tener números, espacios y un + inicial")
    private String telefono;
}
