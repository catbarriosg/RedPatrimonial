package com.redpatrimonial.ms_rutas.services;

import com.redpatrimonial.ms_rutas.dtos.RutaDTO;
import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import com.redpatrimonial.ms_rutas.models.Ruta;

import java.util.List;

public interface RutaService {
    List<Ruta> listar(TipoRuta tipo, Long idRecinto, Long idUsuario);
    Ruta obtenerPorId(Long id);
    Ruta crear(RutaDTO dto);
    Ruta actualizar(Long id, RutaDTO dto);
    void eliminar(Long id);
}
