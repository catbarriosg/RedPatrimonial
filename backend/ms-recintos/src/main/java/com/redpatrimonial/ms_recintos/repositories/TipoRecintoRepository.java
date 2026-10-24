package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.models.TipoRecinto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoRecintoRepository extends JpaRepository<TipoRecinto, Long> {
    Optional<TipoRecinto> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
