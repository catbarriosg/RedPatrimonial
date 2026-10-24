package com.redpatrimonial.ms_recintos.repositories;

import com.redpatrimonial.ms_recintos.models.Multimedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultimediaRepository extends JpaRepository<Multimedia, Long> {
    List<Multimedia> findByRecintoIdOrderByOrdenAsc(Long idRecinto);
}
