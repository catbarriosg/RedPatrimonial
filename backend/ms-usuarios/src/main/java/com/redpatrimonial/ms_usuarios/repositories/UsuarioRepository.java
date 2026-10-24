package com.redpatrimonial.ms_usuarios.repositories;

import com.redpatrimonial.ms_usuarios.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Usuario> findByActivoTrueOrderByNombreAsc();
    List<Usuario> findByRolNombreAndActivoTrueOrderByNombreAsc(String rol);
}
