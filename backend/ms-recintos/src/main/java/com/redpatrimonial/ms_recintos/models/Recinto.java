package com.redpatrimonial.ms_recintos.models;

import com.redpatrimonial.ms_recintos.enums.EstadoApertura;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "recintos")
public class Recinto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre oficial es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    @Column(nullable = false)
    private String nombreOficial;

    @NotNull(message = "La comuna es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_comuna", nullable = false)
    private Comuna comuna;

    @NotNull(message = "El tipo de recinto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoRecinto tipo;

    // Usuarios de ms-usuarios (FK lógica, sin relación entre microservicios)
    private Long idEncargado;
    private Long idAdminRegistro;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitud;

    @NotBlank(message = "La reseña histórica es obligatoria")
    @Column(columnDefinition = "TEXT")
    private String resenaHistorica;

    private String categoria;
    private String estilo;
    private String siglo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoApertura estadoActual = EstadoApertura.ABIERTO;

    private LocalDateTime fechaEstado;

    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (fechaEstado == null) {
            fechaEstado = LocalDateTime.now();
        }
    }
}
