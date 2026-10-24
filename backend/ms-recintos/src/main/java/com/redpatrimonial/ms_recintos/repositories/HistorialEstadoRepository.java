package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.models.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {
    List<HistorialEstado> findByRecintoIdOrderByFechaHoraDesc(Long idRecinto);
}
