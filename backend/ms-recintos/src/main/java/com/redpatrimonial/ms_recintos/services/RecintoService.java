package com.redpatrimonial.ms_recintos.services;

import com.redpatrimonial.ms_recintos.dtos.CambioEstadoDTO;
import com.redpatrimonial.ms_recintos.dtos.RecintoDTO;
import com.redpatrimonial.ms_recintos.enums.EstadoApertura;
import com.redpatrimonial.ms_recintos.models.*;

import java.util.List;

public interface RecintoService {
    List<Recinto> buscar(String texto, Long idComuna, Long idTipo, EstadoApertura estado);
    Recinto obtenerPorId(Long id);
    List<Recinto> obtenerPorEncargado(Long idEncargado);
    Recinto registrar(RecintoDTO dto);
    Recinto actualizar(Long id, RecintoDTO dto);
    Recinto cambiarEstado(Long id, CambioEstadoDTO dto);
    Recinto desactivar(Long id);
    List<HistorialEstado> obtenerHistorial(Long idRecinto);
    AperturaEspecial programarApertura(Long idRecinto, AperturaEspecial apertura);
    List<AperturaEspecial> obtenerAperturas(Long idRecinto);
    Horario agregarHorario(Long idRecinto, Horario horario);
    List<Horario> obtenerHorarios(Long idRecinto);
    Multimedia agregarMultimedia(Long idRecinto, Multimedia multimedia);
    List<Multimedia> obtenerMultimedia(Long idRecinto);
}
