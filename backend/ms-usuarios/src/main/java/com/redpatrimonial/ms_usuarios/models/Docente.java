package com.redpatrimonial.ms_usuarios.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Perfil adicional de un usuario con rol DOCENTE. Comparte el id con el usuario.
@Getter
@Setter
@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    private Long idUsuario;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String establecimiento;

    private String asignatura;
}
