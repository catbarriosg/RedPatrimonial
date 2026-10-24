package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.enums.TipoEvento;
import com.redpatrimonial.ms_recintos.enums.Visibilidad;
import com.redpatrimonial.ms_recintos.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByVisibilidadOrderByFechaInicioAsc(Visibilidad visibilidad);
    List<Evento> findByVisibilidadAndTipoOrderByFechaInicioAsc(Visibilidad visibilidad, TipoEvento tipo);
    List<Evento> findByRecintoIdAndVisibilidadOrderByFechaInicioAsc(Long idRecinto, Visibilidad visibilidad);
    List<Evento> findAllByOrderByFechaPublicacionDesc();
}
