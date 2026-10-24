package com.redpatrimonial.ms_usuarios.repositories;

import com.redpatrimonial.ms_usuarios.models.GuiaVoluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuiaVoluntarioRepository extends JpaRepository<GuiaVoluntario, Long> {
}
