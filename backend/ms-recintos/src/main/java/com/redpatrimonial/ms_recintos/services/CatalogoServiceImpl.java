package com.redpatrimonial.ms_recintos.services;

import com.redpatrimonial.ms_recintos.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_recintos.models.Comuna;
import com.redpatrimonial.ms_recintos.models.TipoRecinto;
import com.redpatrimonial.ms_recintos.repositories.ComunaRepository;
import com.redpatrimonial.ms_recintos.repositories.TipoRecintoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    private static final Logger log = LoggerFactory.getLogger(CatalogoServiceImpl.class);

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private TipoRecintoRepository tipoRepository;

    @Override
    public List<Comuna> obtenerComunas() {
        return comunaRepository.findAll(Sort.by("nombre"));
    }

    @Override
    public Comuna crearComuna(Comuna comuna) {
        if (comunaRepository.existsByNombreIgnoreCase(comuna.getNombre())) {
            throw new ReglaNegocioException("La comuna '" + comuna.getNombre() + "' ya existe.");
        }
        comuna.setId(null);
        Comuna guardada = comunaRepository.save(comuna);
        log.info("[CatalogoService] Comuna registrada: {} (ID {})", guardada.getNombre(), guardada.getId());
        return guardada;
    }

    @Override
    public List<TipoRecinto> obtenerTipos() {
        return tipoRepository.findAll(Sort.by("nombre"));
    }

    @Override
    public TipoRecinto crearTipo(TipoRecinto tipo) {
        if (tipoRepository.existsByNombreIgnoreCase(tipo.getNombre())) {
            throw new ReglaNegocioException("El tipo de recinto '" + tipo.getNombre() + "' ya existe.");
        }
        tipo.setId(null);
        TipoRecinto guardado = tipoRepository.save(tipo);
        log.info("[CatalogoService] Tipo de recinto registrado: {} (ID {})", guardado.getNombre(), guardado.getId());
        return guardado;
    }
}
