package com.redpatrimonial.ms_recintos.services;

import com.redpatrimonial.ms_recintos.models.Comuna;
import com.redpatrimonial.ms_recintos.models.TipoRecinto;

import java.util.List;

public interface CatalogoService {
    List<Comuna> obtenerComunas();
    Comuna crearComuna(Comuna comuna);
    List<TipoRecinto> obtenerTipos();
    TipoRecinto crearTipo(TipoRecinto tipo);
}
