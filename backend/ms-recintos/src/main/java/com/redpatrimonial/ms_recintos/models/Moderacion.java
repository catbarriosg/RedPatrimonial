package com.redpatrimonial.ms_recintos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.redpatrimonial.ms_recintos.enums.AccionModeracion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Registro de cada decisión del administrador sobre un evento (CU-25)
@Getter
@Setter
@Entity
@Table(name = "moderaciones")
public class Moderacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_evento")
    private Evento evento;

    private Long idAdmin;

    @Enumerated(EnumType.STRING)
    private AccionModeracion accion;

    private String motivo;

    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }

    @JsonProperty("idEvento")
    public Long getIdEvento() {
        return evento != null ? evento.getId() : null;
    }
}
