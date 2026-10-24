package com.redpatrimonial.ms_rutas.repositories;

import com.redpatrimonial.ms_rutas.enums.RespuestaAsignacion;
import com.redpatrimonial.ms_rutas.models.AsignacionRecorrido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRecorridoRepository extends JpaRepository<AsignacionRecorrido, Long> {
    List<AsignacionRecorrido> findByIdGuiaAndRespuestaOrderBySolicitudSalidaFechaAsc(Long idGuia, RespuestaAsignacion respuesta);
    List<AsignacionRecorrido> findByIdGuiaOrderByFechaAsignacionDesc(Long idGuia);
}
