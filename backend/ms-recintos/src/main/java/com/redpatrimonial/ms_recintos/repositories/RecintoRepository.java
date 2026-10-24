package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.models.Recinto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecintoRepository extends JpaRepository<Recinto, Long> {
    List<Recinto> findByActivoTrueOrderByNombreOficialAsc();
    List<Recinto> findByIdEncargadoAndActivoTrue(Long idEncargado);
}
