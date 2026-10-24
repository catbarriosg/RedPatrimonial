package com.redpatrimonial.ms_recintos.services;

import com.redpatrimonial.ms_recintos.dtos.CambioEstadoDTO;
import com.redpatrimonial.ms_recintos.dtos.RecintoDTO;
import com.redpatrimonial.ms_recintos.enums.EstadoApertura;
import com.redpatrimonial.ms_recintos.exceptions.RecursoNoEncontradoException;
import com.redpatrimonial.ms_recintos.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_recintos.models.*;
import com.redpatrimonial.ms_recintos.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecintoServiceImpl implements RecintoService {

    private static final Logger log = LoggerFactory.getLogger(RecintoServiceImpl.class);

    @Autowired
    private RecintoRepository repository;

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private TipoRecintoRepository tipoRepository;

    @Autowired
    private HistorialEstadoRepository historialRepository;

    @Autowired
    private AperturaEspecialRepository aperturaRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private MultimediaRepository multimediaRepository;

    @Override
    public List<Recinto> buscar(String texto, Long idComuna, Long idTipo, EstadoApertura estado) {
        log.debug("[RecintoService] Buscando recintos: texto='{}', comuna={}, tipo={}, estado={}", texto, idComuna, idTipo, estado);
        List<Recinto> recintos = repository.findByActivoTrueOrderByNombreOficialAsc().stream()
                .filter(r -> texto == null || texto.isBlank()
                        || r.getNombreOficial().toLowerCase().contains(texto.trim().toLowerCase()))
                .filter(r -> idComuna == null || r.getComuna().getId().equals(idComuna))
                .filter(r -> idTipo == null || r.getTipo().getId().equals(idTipo))
                .filter(r -> estado == null || r.getEstadoActual() == estado)
                .toList();
        log.info("[RecintoService] {} recintos encontrados", recintos.size());
        return recintos;
    }

    @Override
    public Recinto obtenerPorId(Long id) {
        return repository.findById(id)
                .filter(Recinto::getActivo)
                .orElseThrow(() -> {
                    log.warn("[RecintoService] Recinto con ID {} no encontrado", id);
                    return new RecursoNoEncontradoException("Recinto con ID " + id + " no encontrado en la red.");
                });
    }

    @Override
    public List<Recinto> obtenerPorEncargado(Long idEncargado) {
        return repository.findByIdEncargadoAndActivoTrue(idEncargado);
    }

    @Override
    public Recinto registrar(RecintoDTO dto) {
        log.debug("[RecintoService] Registrando nodo: {}", dto.getNombreOficial());
        Recinto recinto = new Recinto();
        copiarDatos(dto, recinto);
        recinto.setIdAdminRegistro(dto.getIdAdminRegistro());
        Recinto guardado = repository.save(recinto);
        log.info("[RecintoService] Recinto '{}' registrado con ID {}", guardado.getNombreOficial(), guardado.getId());
        return guardado;
    }

    @Override
    public Recinto actualizar(Long id, RecintoDTO dto) {
        Recinto recinto = obtenerPorId(id);
        copiarDatos(dto, recinto);
        Recinto actualizado = repository.save(recinto);
        log.info("[RecintoService] Ficha del recinto ID {} actualizada", id);
        return actualizado;
    }

    private void copiarDatos(RecintoDTO dto, Recinto recinto) {
        Comuna comuna = comunaRepository.findById(dto.getIdComuna())
                .orElseThrow(() -> new RecursoNoEncontradoException("Comuna con ID " + dto.getIdComuna() + " no existe."));
        TipoRecinto tipo = tipoRepository.findById(dto.getIdTipo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de recinto con ID " + dto.getIdTipo() + " no existe."));

        recinto.setNombreOficial(dto.getNombreOficial().trim());
        recinto.setComuna(comuna);
        recinto.setTipo(tipo);
        recinto.setIdEncargado(dto.getIdEncargado());
        recinto.setDireccion(dto.getDireccion().trim());
        recinto.setLatitud(dto.getLatitud());
        recinto.setLongitud(dto.getLongitud());
        recinto.setResenaHistorica(dto.getResenaHistorica().trim());
        recinto.setCategoria(dto.getCategoria());
        recinto.setEstilo(dto.getEstilo());
        recinto.setSiglo(dto.getSiglo());
    }

    // CU-17: cada cambio de estado queda registrado en el historial
    @Override
    @Transactional
    public Recinto cambiarEstado(Long id, CambioEstadoDTO dto) {
        Recinto recinto = obtenerPorId(id);
        EstadoApertura anterior = recinto.getEstadoActual();
        if (anterior == dto.getEstado()) {
            throw new ReglaNegocioException("El recinto ya se encuentra en estado " + anterior + ".");
        }

        recinto.setEstadoActual(dto.getEstado());
        recinto.setFechaEstado(LocalDateTime.now());
        Recinto actualizado = repository.save(recinto);

        HistorialEstado historial = new HistorialEstado();
        historial.setRecinto(actualizado);
        historial.setIdUsuario(dto.getIdUsuario());
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(dto.getEstado());
        historialRepository.save(historial);

        log.info("[RecintoService] Estado del recinto '{}' cambiado: {} -> {}", recinto.getNombreOficial(), anterior, dto.getEstado());
        return actualizado;
    }

    @Override
    public Recinto desactivar(Long id) {
        Recinto recinto = obtenerPorId(id);
        recinto.setActivo(false);
        log.info("[RecintoService] Recinto ID {} desactivado", id);
        return repository.save(recinto);
    }

    @Override
    public List<HistorialEstado> obtenerHistorial(Long idRecinto) {
        obtenerPorId(idRecinto);
        return historialRepository.findByRecintoIdOrderByFechaHoraDesc(idRecinto);
    }

    // CU-18
    @Override
    public AperturaEspecial programarApertura(Long idRecinto, AperturaEspecial apertura) {
        Recinto recinto = obtenerPorId(idRecinto);
        if (!apertura.getHoraInicio().isBefore(apertura.getHoraFin())) {
            throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de término.");
        }
        apertura.setId(null);
        apertura.setRecinto(recinto);
        AperturaEspecial guardada = aperturaRepository.save(apertura);
        log.info("[RecintoService] Apertura especial programada para '{}' el {}", recinto.getNombreOficial(), apertura.getFecha());
        return guardada;
    }

    @Override
    public List<AperturaEspecial> obtenerAperturas(Long idRecinto) {
        obtenerPorId(idRecinto);
        return aperturaRepository.findByRecintoIdOrderByFechaAsc(idRecinto);
    }

    @Override
    public Horario agregarHorario(Long idRecinto, Horario horario) {
        Recinto recinto = obtenerPorId(idRecinto);
        if (!horario.getHoraApertura().isBefore(horario.getHoraCierre())) {
            throw new ReglaNegocioException("La hora de apertura debe ser anterior a la hora de cierre.");
        }
        horario.setId(null);
        horario.setRecinto(recinto);
        return horarioRepository.save(horario);
    }

    @Override
    public List<Horario> obtenerHorarios(Long idRecinto) {
        obtenerPorId(idRecinto);
        return horarioRepository.findByRecintoIdOrderByDiaSemanaAsc(idRecinto);
    }

    @Override
    public Multimedia agregarMultimedia(Long idRecinto, Multimedia multimedia) {
        Recinto recinto = obtenerPorId(idRecinto);
        multimedia.setId(null);
        multimedia.setRecinto(recinto);
        return multimediaRepository.save(multimedia);
    }

    @Override
    public List<Multimedia> obtenerMultimedia(Long idRecinto) {
        obtenerPorId(idRecinto);
        return multimediaRepository.findByRecintoIdOrderByOrdenAsc(idRecinto);
    }
}
