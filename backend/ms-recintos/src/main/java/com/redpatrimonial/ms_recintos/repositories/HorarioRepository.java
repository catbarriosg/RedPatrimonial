package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByRecintoIdOrderByDiaSemanaAsc(Long idRecinto);
}
