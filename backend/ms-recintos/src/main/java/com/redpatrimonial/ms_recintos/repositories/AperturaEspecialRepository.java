package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.models.AperturaEspecial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AperturaEspecialRepository extends JpaRepository<AperturaEspecial, Long> {
    List<AperturaEspecial> findByRecintoIdOrderByFechaAsc(Long idRecinto);
}
