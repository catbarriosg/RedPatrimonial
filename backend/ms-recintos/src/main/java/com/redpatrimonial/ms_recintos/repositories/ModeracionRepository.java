package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.models.Moderacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModeracionRepository extends JpaRepository<Moderacion, Long> {
    List<Moderacion> findByEventoIdOrderByFechaDesc(Long idEvento);
}
