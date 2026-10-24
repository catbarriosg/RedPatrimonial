package com.redpatrimonial.ms_usuarios.repositories;

import com.redpatrimonial.ms_usuarios.models.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
}
