package com.redpatrimonial.ms_recintos.services;

import com.redpatrimonial.ms_recintos.dtos.EventoDTO;
import com.redpatrimonial.ms_recintos.dtos.ModeracionDTO;
import com.redpatrimonial.ms_recintos.enums.AccionModeracion;
import com.redpatrimonial.ms_recintos.enums.TipoEvento;
import com.redpatrimonial.ms_recintos.enums.Visibilidad;
import com.redpatrimonial.ms_recintos.exceptions.RecursoNoEncontradoException;
import com.redpatrimonial.ms_recintos.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_recintos.models.Evento;
import com.redpatrimonial.ms_recintos.models.Moderacion;
import com.redpatrimonial.ms_recintos.models.Recinto;
import com.redpatrimonial.ms_recintos.repositories.EventoRepository;
import com.redpatrimonial.ms_recintos.repositories.ModeracionRepository;
import com.redpatrimonial.ms_recintos.util.Distancia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class EventoServiceImpl implements EventoService {

    private static final Logger log = LoggerFactory.getLogger(EventoServiceImpl.class);

    @Autowired
    private EventoRepository repository;

    @Autowired
    private ModeracionRepository moderacionRepository;

    @Autowired
    private RecintoService recintoService;

    @Override
    public List<Evento> obtenerVisibles(TipoEvento tipo) {
        if (tipo == null) {
            return repository.findByVisibilidadOrderByFechaInicioAsc(Visibilidad.VISIBLE);
        }
        return repository.findByVisibilidadAndTipoOrderByFechaInicioAsc(Visibilidad.VISIBLE, tipo);
    }

    // CU-06: eventos visibles dentro del radio, del más cercano al más lejano
    @Override
    public List<Evento> obtenerCercanos(double latitud, double longitud, double radioKm) {
        if (radioKm <= 0) {
            throw new ReglaNegocioException("El radio de búsqueda debe ser mayor a 0 km.");
        }
        List<Evento> cercanos = obtenerVisibles(null).stream()
                .filter(e -> distanciaKm(e, latitud, longitud) <= radioKm)
                .sorted(Comparator.comparingDouble(e -> distanciaKm(e, latitud, longitud)))
                .toList();
        log.info("[EventoService] {} eventos a menos de {} km de ({}, {})", cercanos.size(), radioKm, latitud, longitud);
        return cercanos;
    }

    private double distanciaKm(Evento evento, double latitud, double longitud) {
        return Distancia.kilometros(latitud, longitud, evento.getLatitud(), evento.getLongitud());
    }

    @Override
    public List<Evento> obtenerTodos() {
        return repository.findAllByOrderByFechaPublicacionDesc();
    }

    @Override
    public List<Evento> obtenerPorRecinto(Long idRecinto) {
        recintoService.obtenerPorId(idRecinto);
        return repository.findByRecintoIdAndVisibilidadOrderByFechaInicioAsc(idRecinto, Visibilidad.VISIBLE);
    }

    @Override
    public Evento obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento con ID " + id + " no encontrado."));
    }

    // CU-19
    @Override
    public Evento publicar(EventoDTO dto) {
        Evento evento = new Evento();
        copiarDatos(dto, evento);
        evento.setIdAutor(dto.getIdAutor());
        Evento guardado = repository.save(evento);
        log.info("[EventoService] Evento '{}' publicado en recinto ID {}", guardado.getTitulo(), dto.getIdRecinto());
        return guardado;
    }

    @Override
    public Evento actualizar(Long id, EventoDTO dto) {
        Evento evento = obtenerPorId(id);
        copiarDatos(dto, evento);
        return repository.save(evento);
    }

    private void copiarDatos(EventoDTO dto, Evento evento) {
        if (dto.getFechaFin() != null && !dto.getFechaFin().isAfter(dto.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha de término debe ser posterior a la fecha de inicio.");
        }
        Recinto recinto = recintoService.obtenerPorId(dto.getIdRecinto());
        evento.setRecinto(recinto);
        evento.setTitulo(dto.getTitulo().trim());
        evento.setDescripcion(dto.getDescripcion());
        evento.setTipo(dto.getTipo());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        // El evento queda indexado en la ubicación de su recinto
        evento.setLatitud(recinto.getLatitud());
        evento.setLongitud(recinto.getLongitud());
    }

    @Override
    public void eliminar(Long id) {
        Evento evento = obtenerPorId(id);
        repository.delete(evento);
        log.info("[EventoService] Evento ID {} eliminado", id);
    }

    // CU-25: ocultar o volver a aprobar una publicación, dejando registro
    @Override
    @Transactional
    public Evento moderar(Long id, ModeracionDTO dto) {
        Evento evento = obtenerPorId(id);
        evento.setVisibilidad(dto.getAccion() == AccionModeracion.OCULTAR ? Visibilidad.OCULTO : Visibilidad.VISIBLE);
        Evento actualizado = repository.save(evento);

        Moderacion moderacion = new Moderacion();
        moderacion.setEvento(actualizado);
        moderacion.setIdAdmin(dto.getIdAdmin());
        moderacion.setAccion(dto.getAccion());
        moderacion.setMotivo(dto.getMotivo());
        moderacionRepository.save(moderacion);

        log.info("[EventoService] Evento ID {} moderado: {}", id, dto.getAccion());
        return actualizado;
    }

    @Override
    public List<Moderacion> obtenerModeraciones(Long id) {
        obtenerPorId(id);
        return moderacionRepository.findByEventoIdOrderByFechaDesc(id);
    }
}
