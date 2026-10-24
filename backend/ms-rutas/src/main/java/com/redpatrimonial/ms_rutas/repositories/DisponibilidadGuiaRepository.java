package com.redpatrimonial.ms_rutas.repositories;

import com.redpatrimonial.ms_rutas.models.DisponibilidadGuia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibilidadGuiaRepository extends JpaRepository<DisponibilidadGuia, Long> {
    List<DisponibilidadGuia> findByIdGuiaOrderByDiaSemanaAscHoraInicioAsc(Long idGuia);
    List<DisponibilidadGuia> findByIdGuiaAndDiaSemana(Long idGuia, Integer diaSemana);
}
