package com.redpatrimonial.ms_recintos.models;

import com.redpatrimonial.ms_recintos.enums.TipoEvento;
import com.redpatrimonial.ms_recintos.enums.Visibilidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

// Evento o aviso publicado por un recinto, indexado por su ubicación (CU-19)
@Data
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_recinto", nullable = false)
    private Recinto recinto;

    private Long idAutor;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 120, message = "El título no puede superar 120 caracteres")
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El tipo de evento es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoEvento tipo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    // Si no se indican, se usan las coordenadas del recinto
    private Double latitud;
    private Double longitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibilidad visibilidad = Visibilidad.VISIBLE;

    @Column(updatable = false)
    private LocalDateTime fechaPublicacion;

    @PrePersist
    protected void onCreate() {
        fechaPublicacion = LocalDateTime.now();
    }
}
