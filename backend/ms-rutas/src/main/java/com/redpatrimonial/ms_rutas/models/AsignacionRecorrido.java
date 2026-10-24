package com.redpatrimonial.ms_rutas.models;

import com.redpatrimonial.ms_rutas.enums.RespuestaAsignacion;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asignaciones_recorrido")
public class AsignacionRecorrido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_solicitud")
    private SolicitudGuia solicitud;

    @Column(nullable = false)
    private Long idGuia;

    // Encargado que hizo la asignación (FK lógica a ms-usuarios)
    private Long idEncargado;

    @Column(updatable = false)
    private LocalDateTime fechaAsignacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RespuestaAsignacion respuesta = RespuestaAsignacion.PENDIENTE;

    private LocalDateTime fechaRespuesta;

    private String motivoRechazo;

    @PrePersist
    protected void onCreate() {
        fechaAsignacion = LocalDateTime.now();
    }
}
