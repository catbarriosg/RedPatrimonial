package com.redpatrimonial.ms_rutas.services;

import com.redpatrimonial.ms_rutas.dtos.SalidaDTO;
import com.redpatrimonial.ms_rutas.models.SalidaPedagogica;

import java.util.List;

public interface SalidaService {
    List<SalidaPedagogica> listarPorDocente(Long idDocente);
    SalidaPedagogica obtenerPorId(Long id);
    SalidaPedagogica planificar(SalidaDTO dto);
}
