package com.redpatrimonial.ms_rutas.models;

import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "rutas")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario de ms-usuarios que creó la ruta (FK lógica)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRuta tipo;

    // Suma de traslados y visitas de todas las paradas (CU-10)
    private Integer duracionTotalMin;

    private Double distanciaKm;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<ParadaRuta> paradas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
