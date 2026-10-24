package com.redpatrimonial.ms_rutas.repositories;

import com.redpatrimonial.ms_rutas.enums.EstadoSolicitud;
import com.redpatrimonial.ms_rutas.models.SolicitudGuia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudGuiaRepository extends JpaRepository<SolicitudGuia, Long> {
    List<SolicitudGuia> findBySalidaIdDocenteOrderBySalidaFechaAsc(Long idDocente);
    List<SolicitudGuia> findByIdRecintoOrderBySalidaFechaAsc(Long idRecinto);
    List<SolicitudGuia> findByEstadoOrderBySalidaFechaAsc(EstadoSolicitud estado);
}
