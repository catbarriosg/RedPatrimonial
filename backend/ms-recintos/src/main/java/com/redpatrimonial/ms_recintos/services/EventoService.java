package com.redpatrimonial.ms_recintos.services;

import com.redpatrimonial.ms_recintos.dtos.EventoDTO;
import com.redpatrimonial.ms_recintos.dtos.ModeracionDTO;
import com.redpatrimonial.ms_recintos.enums.TipoEvento;
import com.redpatrimonial.ms_recintos.models.Evento;
import com.redpatrimonial.ms_recintos.models.Moderacion;

import java.util.List;

public interface EventoService {
    List<Evento> obtenerVisibles(TipoEvento tipo);
    List<Evento> obtenerCercanos(double latitud, double longitud, double radioKm);
    List<Evento> obtenerTodos();
    List<Evento> obtenerPorRecinto(Long idRecinto);
    Evento obtenerPorId(Long id);
    Evento publicar(EventoDTO dto);
    Evento actualizar(Long id, EventoDTO dto);
    void eliminar(Long id);
    Evento moderar(Long id, ModeracionDTO dto);
    List<Moderacion> obtenerModeraciones(Long id);
}
