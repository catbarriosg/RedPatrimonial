package com.redpatrimonial.ms_usuarios.services;

import com.redpatrimonial.ms_usuarios.dtos.ActualizarUsuarioDTO;
import com.redpatrimonial.ms_usuarios.dtos.LoginDTO;
import com.redpatrimonial.ms_usuarios.dtos.RegistroUsuarioDTO;
import com.redpatrimonial.ms_usuarios.dtos.SesionDTO;
import com.redpatrimonial.ms_usuarios.models.Rol;
import com.redpatrimonial.ms_usuarios.models.Usuario;

import java.util.List;

public interface UsuarioService {
    List<Rol> obtenerRoles();
    List<Usuario> obtenerTodos(String rol);
    Usuario obtenerPorId(Long id);
    Usuario registrar(RegistroUsuarioDTO dto);
    Usuario actualizar(Long id, ActualizarUsuarioDTO dto);
    Usuario desactivar(Long id);
    SesionDTO login(LoginDTO dto);
}
