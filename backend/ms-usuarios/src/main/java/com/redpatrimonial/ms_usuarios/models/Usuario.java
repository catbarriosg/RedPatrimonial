package com.redpatrimonial.ms_usuarios.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @Column(nullable = false)
    private String nombre;

    private String apellido;

    @Column(unique = true, nullable = false)
    private String email;

    // Nunca se devuelve en las respuestas JSON
    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
