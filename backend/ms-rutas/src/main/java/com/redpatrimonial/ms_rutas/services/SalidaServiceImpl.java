package com.redpatrimonial.ms_rutas.services;

import com.redpatrimonial.ms_rutas.dtos.SalidaDTO;
import com.redpatrimonial.ms_rutas.exceptions.RecursoNoEncontradoException;
import com.redpatrimonial.ms_rutas.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_rutas.models.Ruta;
import com.redpatrimonial.ms_rutas.models.SalidaPedagogica;
import com.redpatrimonial.ms_rutas.models.SolicitudGuia;
import com.redpatrimonial.ms_rutas.repositories.SalidaPedagogicaRepository;
import com.redpatrimonial.ms_rutas.repositories.SolicitudGuiaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalidaServiceImpl implements SalidaService {

    private static final Logger log = LoggerFactory.getLogger(SalidaServiceImpl.class);

    @Autowired
    private SalidaPedagogicaRepository repository;

    @Autowired
    private SolicitudGuiaRepository solicitudRepository;

    @Autowired
    private RutaService rutaService;

    @Override
    public List<SalidaPedagogica> listarPorDocente(Long idDocente) {
        return repository.findByIdDocenteOrderByFechaAsc(idDocente);
    }

    @Override
    public SalidaPedagogica obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Salida pedagógica con ID " + id + " no encontrada."));
    }

    // La salida se construye sobre una ruta (R-05) y puede incluir una solicitud de guía (R-06)
    @Override
    @Transactional
    public SalidaPedagogica planificar(SalidaDTO dto) {
        Ruta ruta = rutaService.obtenerPorId(dto.getIdRuta());

        if (dto.isSolicitarGuia()) {
            if (dto.getIdRecintoGuia() == null) {
                throw new ReglaNegocioException("Indica en qué recinto se necesita el guía.");
            }
            boolean recintoEnRuta = ruta.getParadas().stream()
                    .anyMatch(p -> p.getIdRecinto().equals(dto.getIdRecintoGuia()));
            if (!recintoEnRuta) {
                throw new ReglaNegocioException("El recinto " + dto.getIdRecintoGuia() + " no es parada de la ruta elegida.");
            }
        }

        SalidaPedagogica salida = new SalidaPedagogica();
        salida.setIdDocente(dto.getIdDocente());
        salida.setRuta(ruta);
        salida.setFecha(dto.getFecha());
        salida.setHoraInicio(dto.getHoraInicio());
        salida.setTamanoGrupo(dto.getTamanoGrupo());
        salida.setNivelEducativo(dto.getNivelEducativo());
        salida.setObservaciones(dto.getObservaciones());
        SalidaPedagogica guardada = repository.save(salida);

        if (dto.isSolicitarGuia()) {
            SolicitudGuia solicitud = new SolicitudGuia();
            solicitud.setSalida(guardada);
            solicitud.setIdRecinto(dto.getIdRecintoGuia());
            solicitud.setComentario(dto.getComentario());
            solicitudRepository.save(solicitud);
        }

        log.info("[SalidaService] Salida del docente {} planificada para el {} (guía: {})",
                dto.getIdDocente(), dto.getFecha(), dto.isSolicitarGuia());
        return guardada;
    }
}
