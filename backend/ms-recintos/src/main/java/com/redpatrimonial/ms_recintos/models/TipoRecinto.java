package com.redpatrimonial.ms_recintos.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Templo, museo, colegio, archivo o institución
@Data
@Entity
@Table(name = "tipos_recinto")
public class TipoRecinto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tipo es obligatorio")
    @Size(max = 40, message = "El nombre no puede superar 40 caracteres")
    @Column(unique = true, nullable = false)
    private String nombre;
}
