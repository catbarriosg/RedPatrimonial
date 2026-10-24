package com.redpatrimonial.ms_rutas.services;

import com.redpatrimonial.ms_rutas.dtos.AsignacionDTO;
import com.redpatrimonial.ms_rutas.dtos.RespuestaDTO;
import com.redpatrimonial.ms_rutas.enums.EstadoSolicitud;
import com.redpatrimonial.ms_rutas.enums.RespuestaAsignacion;
import com.redpatrimonial.ms_rutas.exceptions.RecursoNoEncontradoException;
import com.redpatrimonial.ms_rutas.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_rutas.models.AsignacionRecorrido;
import com.redpatrimonial.ms_rutas.models.DisponibilidadGuia;
import com.redpatrimonial.ms_rutas.models.SalidaPedagogica;
import com.redpatrimonial.ms_rutas.models.SolicitudGuia;
import com.redpatrimonial.ms_rutas.repositories.AsignacionRecorridoRepository;
import com.redpatrimonial.ms_rutas.repositories.DisponibilidadGuiaRepository;
import com.redpatrimonial.ms_rutas.repositories.SolicitudGuiaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// CU-13 a CU-14 y CU-20 a CU-23: solicitudes, asignaciones, disponibilidad y agenda de guías
@Service
public class CoordinacionGuiasServiceImpl implements CoordinacionGuiasService {

    private static final Logger log = LoggerFactory.getLogger(CoordinacionGuiasServiceImpl.class);

    @Autowired
    private SolicitudGuiaRepository solicitudRepository;

    @Autowired
    private AsignacionRecorridoRepository asignacionRepository;

    @Autowired
    private DisponibilidadGuiaRepository disponibilidadRepository;

    @Override
    public List<SolicitudGuia> listarSolicitudes(Long idDocente, Long idRecinto, EstadoSolicitud estado) {
        if (idDocente != null) {
            return solicitudRepository.findBySalidaIdDocenteOrderBySalidaFechaAsc(idDocente);
        }
        if (idRecinto != null) {
            return solicitudRepository.findByIdRecintoOrderBySalidaFechaAsc(idRecinto);
        }
        return solicitudRepository.findByEstadoOrderBySalidaFechaAsc(estado != null ? estado : EstadoSolicitud.PENDIENTE);
    }

    @Override
    public SolicitudGuia obtenerSolicitud(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud de guía con ID " + id + " no encontrada."));
    }

    @Override
    public SolicitudGuia rechazarSolicitud(Long id) {
        SolicitudGuia solicitud = obtenerSolicitud(id);
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden rechazar solicitudes pendientes.");
        }
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        log.info("[CoordinacionGuias] Solicitud ID {} rechazada", id);
        return solicitudRepository.save(solicitud);
    }

    // CU-20: solo se asignan solicitudes pendientes y a un guía disponible ese día y hora
    @Override
    @Transactional
    public AsignacionRecorrido asignarGuia(AsignacionDTO dto) {
        SolicitudGuia solicitud = obtenerSolicitud(dto.getIdSolicitud());
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("La solicitud " + solicitud.getId() + " ya fue " + solicitud.getEstado().name().toLowerCase() + ".");
        }

        SalidaPedagogica salida = solicitud.getSalida();
        int diaSemana = salida.getFecha().getDayOfWeek().getValue();
        if (!guiaDisponible(dto.getIdGuia(), diaSemana, salida.getHoraInicio())) {
            throw new ReglaNegocioException("El guía " + dto.getIdGuia() + " no tiene disponibilidad el "
                    + salida.getFecha().getDayOfWeek() + " a las " + salida.getHoraInicio() + ".");
        }

        solicitud.setEstado(EstadoSolicitud.ASIGNADA);
        solicitudRepository.save(solicitud);

        AsignacionRecorrido asignacion = new AsignacionRecorrido();
        asignacion.setSolicitud(solicitud);
        asignacion.setIdGuia(dto.getIdGuia());
        asignacion.setIdEncargado(dto.getIdEncargado());
        AsignacionRecorrido guardada = asignacionRepository.save(asignacion);
        log.info("[CoordinacionGuias] Guía {} asignado a la solicitud {}", dto.getIdGuia(), solicitud.getId());
        return guardada;
    }

    private boolean guiaDisponible(Long idGuia, int diaSemana, LocalTime hora) {
        return disponibilidadRepository.findByIdGuiaAndDiaSemana(idGuia, diaSemana).stream()
                .anyMatch(d -> !hora.isBefore(d.getHoraInicio()) && hora.isBefore(d.getHoraFin()));
    }

    // CU-22: si el guía rechaza, la solicitud vuelve a quedar pendiente para otro guía
    @Override
    @Transactional
    public AsignacionRecorrido responder(Long idAsignacion, RespuestaDTO dto) {
        AsignacionRecorrido asignacion = asignacionRepository.findById(idAsignacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación con ID " + idAsignacion + " no encontrada."));
        if (asignacion.getRespuesta() != RespuestaAsignacion.PENDIENTE) {
            throw new ReglaNegocioException("Esta asignación ya fue respondida.");
        }

        asignacion.setFechaRespuesta(LocalDateTime.now());
        if (dto.isAcepta()) {
            asignacion.setRespuesta(RespuestaAsignacion.ACEPTADA);
        } else {
            asignacion.setRespuesta(RespuestaAsignacion.RECHAZADA);
            asignacion.setMotivoRechazo(dto.getMotivoRechazo());
            SolicitudGuia solicitud = asignacion.getSolicitud();
            solicitud.setEstado(EstadoSolicitud.PENDIENTE);
            solicitudRepository.save(solicitud);
        }
        log.info("[CoordinacionGuias] Asignación {} respondida: {}", idAsignacion, asignacion.getRespuesta());
        return asignacionRepository.save(asignacion);
    }

    @Override
    public List<AsignacionRecorrido> listarAsignaciones(Long idGuia) {
        return asignacionRepository.findByIdGuiaOrderByFechaAsignacionDesc(idGuia);
    }

    // CU-23
    @Override
    public List<AsignacionRecorrido> obtenerAgenda(Long idGuia) {
        return asignacionRepository.findByIdGuiaAndRespuestaOrderBySolicitudSalidaFechaAsc(idGuia, RespuestaAsignacion.ACEPTADA);
    }

    @Override
    public List<DisponibilidadGuia> listarDisponibilidad(Long idGuia) {
        return disponibilidadRepository.findByIdGuiaOrderByDiaSemanaAscHoraInicioAsc(idGuia);
    }

    // CU-21
    @Override
    public DisponibilidadGuia agregarDisponibilidad(Long idGuia, DisponibilidadGuia disponibilidad) {
        if (!disponibilidad.getHoraInicio().isBefore(disponibilidad.getHoraFin())) {
            throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de término.");
        }
        disponibilidad.setId(null);
        disponibilidad.setIdGuia(idGuia);
        return disponibilidadRepository.save(disponibilidad);
    }

    @Override
    public void eliminarDisponibilidad(Long id) {
        if (!disponibilidadRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Bloque de disponibilidad con ID " + id + " no encontrado.");
        }
        disponibilidadRepository.deleteById(id);
    }
}
