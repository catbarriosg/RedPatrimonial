package com.redpatrimonial.ms_rutas.services;

import com.redpatrimonial.ms_rutas.dtos.AsignacionDTO;
import com.redpatrimonial.ms_rutas.dtos.RespuestaDTO;
import com.redpatrimonial.ms_rutas.enums.EstadoSolicitud;
import com.redpatrimonial.ms_rutas.models.AsignacionRecorrido;
import com.redpatrimonial.ms_rutas.models.DisponibilidadGuia;
import com.redpatrimonial.ms_rutas.models.SolicitudGuia;

import java.util.List;

public interface CoordinacionGuiasService {
    List<SolicitudGuia> listarSolicitudes(Long idDocente, Long idRecinto, EstadoSolicitud estado);
    SolicitudGuia obtenerSolicitud(Long id);
    SolicitudGuia rechazarSolicitud(Long id);
    AsignacionRecorrido asignarGuia(AsignacionDTO dto);
    AsignacionRecorrido responder(Long idAsignacion, RespuestaDTO dto);
    List<AsignacionRecorrido> listarAsignaciones(Long idGuia);
    List<AsignacionRecorrido> obtenerAgenda(Long idGuia);
    List<DisponibilidadGuia> listarDisponibilidad(Long idGuia);
    DisponibilidadGuia agregarDisponibilidad(Long idGuia, DisponibilidadGuia disponibilidad);
    void eliminarDisponibilidad(Long id);
}
