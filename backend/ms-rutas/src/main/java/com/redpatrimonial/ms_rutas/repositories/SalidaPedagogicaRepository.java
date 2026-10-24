package com.redpatrimonial.ms_rutas.repositories;

import com.redpatrimonial.ms_rutas.models.SalidaPedagogica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalidaPedagogicaRepository extends JpaRepository<SalidaPedagogica, Long> {
    List<SalidaPedagogica> findByIdDocenteOrderByFechaAsc(Long idDocente);
}
