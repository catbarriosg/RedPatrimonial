package com.redpatrimonial.ms_recintos.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "comunas")
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la comuna es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
    @Column(unique = true, nullable = false)
    private String nombre;
}
