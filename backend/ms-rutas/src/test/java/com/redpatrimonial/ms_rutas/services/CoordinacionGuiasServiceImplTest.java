package com.redpatrimonial.ms_rutas.services;

import com.redpatrimonial.ms_rutas.dtos.AsignacionDTO;
import com.redpatrimonial.ms_rutas.dtos.RespuestaDTO;
import com.redpatrimonial.ms_rutas.enums.EstadoSolicitud;
import com.redpatrimonial.ms_rutas.enums.RespuestaAsignacion;
import com.redpatrimonial.ms_rutas.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_rutas.models.AsignacionRecorrido;
import com.redpatrimonial.ms_rutas.models.DisponibilidadGuia;
import com.redpatrimonial.ms_rutas.models.SalidaPedagogica;
import com.redpatrimonial.ms_rutas.models.SolicitudGuia;
import com.redpatrimonial.ms_rutas.repositories.AsignacionRecorridoRepository;
import com.redpatrimonial.ms_rutas.repositories.DisponibilidadGuiaRepository;
import com.redpatrimonial.ms_rutas.repositories.SolicitudGuiaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoordinacionGuiasServiceImplTest {

    @Mock
    private SolicitudGuiaRepository solicitudRepository;

    @Mock
    private AsignacionRecorridoRepository asignacionRepository;

    @Mock
    private DisponibilidadGuiaRepository disponibilidadRepository;

    @InjectMocks
    private CoordinacionGuiasServiceImpl service;

    // Lunes 9 de noviembre de 2026 a las 10:00
    private SolicitudGuia solicitudPendiente() {
        SalidaPedagogica salida = new SalidaPedagogica();
        salida.setFecha(LocalDate.of(2026, 11, 9));
        salida.setHoraInicio(LocalTime.of(10, 0));
        SolicitudGuia solicitud = new SolicitudGuia();
        solicitud.setId(1L);
        solicitud.setSalida(salida);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        return solicitud;
    }

    private DisponibilidadGuia bloqueLunes(int desde, int hasta) {
        DisponibilidadGuia bloque = new DisponibilidadGuia();
        bloque.setDiaSemana(1);
        bloque.setHoraInicio(LocalTime.of(desde, 0));
        bloque.setHoraFin(LocalTime.of(hasta, 0));
        return bloque;
    }

    private AsignacionDTO asignacion() {
        AsignacionDTO dto = new AsignacionDTO();
        dto.setIdSolicitud(1L);
        dto.setIdGuia(4L);
        return dto;
    }

    @Test
    void asignarGuiaDisponibleMarcaLaSolicitudComoAsignada() {
        SolicitudGuia solicitud = solicitudPendiente();
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(disponibilidadRepository.findByIdGuiaAndDiaSemana(4L, 1)).thenReturn(List.of(bloqueLunes(9, 13)));
        when(asignacionRepository.save(any(AsignacionRecorrido.class))).thenAnswer(inv -> inv.getArgument(0));

        AsignacionRecorrido creada = service.asignarGuia(asignacion());

        assertEquals(EstadoSolicitud.ASIGNADA, solicitud.getEstado());
        assertEquals(RespuestaAsignacion.PENDIENTE, creada.getRespuesta());
    }

    @Test
    void noSeAsignaUnGuiaSinDisponibilidadEnEsaHora() {
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudPendiente()));
        when(disponibilidadRepository.findByIdGuiaAndDiaSemana(4L, 1)).thenReturn(List.of(bloqueLunes(15, 18)));

        assertThrows(ReglaNegocioException.class, () -> service.asignarGuia(asignacion()));
        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void noSeAsignaUnaSolicitudQueNoEstaPendiente() {
        SolicitudGuia solicitud = solicitudPendiente();
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        assertThrows(ReglaNegocioException.class, () -> service.asignarGuia(asignacion()));
    }

    @Test
    void siElGuiaRechazaLaSolicitudVuelveAPendiente() {
        SolicitudGuia solicitud = solicitudPendiente();
        solicitud.setEstado(EstadoSolicitud.ASIGNADA);
        AsignacionRecorrido asignacion = new AsignacionRecorrido();
        asignacion.setSolicitud(solicitud);
        when(asignacionRepository.findById(7L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any(AsignacionRecorrido.class))).thenAnswer(inv -> inv.getArgument(0));

        RespuestaDTO respuesta = new RespuestaDTO();
        respuesta.setAcepta(false);
        respuesta.setMotivoRechazo("Tengo otro recorrido");

        AsignacionRecorrido respondida = service.responder(7L, respuesta);

        assertEquals(RespuestaAsignacion.RECHAZADA, respondida.getRespuesta());
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
    }
}
