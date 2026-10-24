package com.redpatrimonial.ms_usuarios.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Pattern(regexp = "^(DOCENTE|ENCARGADO|GUIA|ADMIN)$", message = "El rol debe ser: DOCENTE, ENCARGADO, GUIA o ADMIN")
    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;
}
