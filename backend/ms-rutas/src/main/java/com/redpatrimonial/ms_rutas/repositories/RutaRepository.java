package com.redpatrimonial.ms_rutas.repositories;

import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import com.redpatrimonial.ms_rutas.models.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findByTipoOrderByNombreAsc(TipoRuta tipo);
    List<Ruta> findByIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);
    List<Ruta> findDistinctByParadasIdRecinto(Long idRecinto);
}
