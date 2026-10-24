package com.redpatrimonial.ms_rutas.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "salidas_pedagogicas")
public class SalidaPedagogica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idDocente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private Integer tamanoGrupo;

    private String nivelEducativo;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
