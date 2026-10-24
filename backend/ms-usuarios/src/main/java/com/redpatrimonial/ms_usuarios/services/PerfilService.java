package com.redpatrimonial.ms_usuarios.services;

import com.redpatrimonial.ms_usuarios.dtos.PerfilDocenteDTO;
import com.redpatrimonial.ms_usuarios.dtos.PerfilGuiaDTO;
import com.redpatrimonial.ms_usuarios.models.Docente;
import com.redpatrimonial.ms_usuarios.models.GuiaVoluntario;

import java.util.List;

public interface PerfilService {
    List<Docente> obtenerDocentes();
    Docente obtenerDocente(Long idUsuario);
    Docente guardarDocente(Long idUsuario, PerfilDocenteDTO dto);
    List<GuiaVoluntario> obtenerGuias();
    GuiaVoluntario obtenerGuia(Long idUsuario);
    GuiaVoluntario guardarGuia(Long idUsuario, PerfilGuiaDTO dto);
}
