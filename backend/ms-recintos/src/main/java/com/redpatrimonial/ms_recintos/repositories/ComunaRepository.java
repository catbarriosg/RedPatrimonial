package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.models.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {
    Optional<Comuna> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
