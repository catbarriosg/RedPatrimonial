package com.redpatrimonial.ms_rutas.models;

import com.redpatrimonial.ms_rutas.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "solicitudes_guia")
public class SolicitudGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_salida")
    private SalidaPedagogica salida;

    // Recinto donde se necesita el guía (FK lógica a ms-recintos)
    @Column(nullable = false)
    private Long idRecinto;

    @Column(updatable = false)
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    private String comentario;

    @PrePersist
    protected void onCreate() {
        fechaSolicitud = LocalDateTime.now();
    }
}
