package com.redpatrimonial.ms_rutas.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "paradas_ruta")
public class ParadaRuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    // Recinto de ms-recintos (FK lógica)
    @Column(nullable = false)
    private Long idRecinto;

    @Column(nullable = false)
    private Integer orden;

    // Minutos caminando desde la parada anterior (0 en la primera)
    private Integer trasladoDesdeAnteriorMin;

    private Integer tiempoVisitaMin;
}
