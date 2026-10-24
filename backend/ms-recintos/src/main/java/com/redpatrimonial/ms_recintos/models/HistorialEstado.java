package com.redpatrimonial.ms_recintos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.redpatrimonial.ms_recintos.enums.EstadoApertura;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "historial_estado")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_recinto")
    private Recinto recinto;

    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    private EstadoApertura estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoApertura estadoNuevo;

    private LocalDateTime fechaHora;

    @PrePersist
    protected void onCreate() {
        fechaHora = LocalDateTime.now();
    }

    @JsonProperty("idRecinto")
    public Long getIdRecinto() {
        return recinto != null ? recinto.getId() : null;
    }
}
