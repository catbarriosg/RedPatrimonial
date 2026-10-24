package com.redpatrimonial.ms_rutas.services;

import com.redpatrimonial.ms_rutas.dtos.ParadaDTO;
import com.redpatrimonial.ms_rutas.dtos.RutaDTO;
import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import com.redpatrimonial.ms_rutas.exceptions.RecursoNoEncontradoException;
import com.redpatrimonial.ms_rutas.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_rutas.models.ParadaRuta;
import com.redpatrimonial.ms_rutas.models.Ruta;
import com.redpatrimonial.ms_rutas.repositories.RutaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RutaServiceImpl implements RutaService {

    private static final Logger log = LoggerFactory.getLogger(RutaServiceImpl.class);

    @Autowired
    private RutaRepository repository;

    @Override
    public List<Ruta> listar(TipoRuta tipo, Long idRecinto, Long idUsuario) {
        if (idRecinto != null) {
            return repository.findDistinctByParadasIdRecinto(idRecinto);
        }
        if (idUsuario != null) {
            return repository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario);
        }
        return repository.findByTipoOrderByNombreAsc(tipo != null ? tipo : TipoRuta.OFICIAL);
    }

    @Override
    public Ruta obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta con ID " + id + " no encontrada."));
    }

    @Override
    @Transactional
    public Ruta crear(RutaDTO dto) {
        Ruta ruta = new Ruta();
        copiarDatos(dto, ruta);
        Ruta guardada = repository.save(ruta);
        log.info("[RutaService] Ruta {} '{}' creada con {} paradas y {} min", guardada.getTipo(), guardada.getNombre(),
                guardada.getParadas().size(), guardada.getDuracionTotalMin());
        return guardada;
    }

    @Override
    @Transactional
    public Ruta actualizar(Long id, RutaDTO dto) {
        Ruta ruta = obtenerPorId(id);
        copiarDatos(dto, ruta);
        return repository.save(ruta);
    }

    // Reemplaza las paradas y recalcula la duración total (CU-10)
    private void copiarDatos(RutaDTO dto, Ruta ruta) {
        Set<Long> recintos = new HashSet<>();
        for (ParadaDTO parada : dto.getParadas()) {
            if (!recintos.add(parada.getIdRecinto())) {
                throw new ReglaNegocioException("El recinto " + parada.getIdRecinto() + " está repetido en la ruta.");
            }
        }

        ruta.setIdUsuario(dto.getIdUsuario());
        ruta.setNombre(dto.getNombre().trim());
        ruta.setDescripcion(dto.getDescripcion());
        ruta.setTipo(dto.getTipo());
        ruta.setDistanciaKm(dto.getDistanciaKm());

        ruta.getParadas().clear();
        int duracion = 0;
        for (int i = 0; i < dto.getParadas().size(); i++) {
            ParadaDTO datos = dto.getParadas().get(i);
            ParadaRuta parada = new ParadaRuta();
            parada.setRuta(ruta);
            parada.setIdRecinto(datos.getIdRecinto());
            parada.setOrden(i + 1);
            // La primera parada no tiene traslado previo
            parada.setTrasladoDesdeAnteriorMin(i == 0 ? 0 : valor(datos.getTrasladoDesdeAnteriorMin(), 0));
            parada.setTiempoVisitaMin(valor(datos.getTiempoVisitaMin(), 20));
            duracion += parada.getTrasladoDesdeAnteriorMin() + parada.getTiempoVisitaMin();
            ruta.getParadas().add(parada);
        }
        ruta.setDuracionTotalMin(duracion);
    }

    private int valor(Integer numero, int porDefecto) {
        return numero != null ? numero : porDefecto;
    }

    @Override
    public void eliminar(Long id) {
        repository.delete(obtenerPorId(id));
        log.info("[RutaService] Ruta ID {} eliminada", id);
    }
}
