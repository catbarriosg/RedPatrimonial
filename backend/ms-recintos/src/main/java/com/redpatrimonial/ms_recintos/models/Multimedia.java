package com.redpatrimonial.ms_recintos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.redpatrimonial.ms_recintos.enums.TipoMultimedia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// Galería de recursos simulados de cada recinto
@Getter
@Setter
@Entity
@Table(name = "multimedia")
public class Multimedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_recinto")
    private Recinto recinto;

    @NotNull(message = "El tipo de recurso es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoMultimedia tipo;

    @NotBlank(message = "La URL del recurso es obligatoria")
    private String url;

    private String descripcion;

    private Integer orden;

    @JsonProperty("idRecinto")
    public Long getIdRecinto() {
        return recinto != null ? recinto.getId() : null;
    }
}
