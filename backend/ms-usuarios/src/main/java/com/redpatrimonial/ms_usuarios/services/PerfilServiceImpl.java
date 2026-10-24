package com.redpatrimonial.ms_usuarios.services;

import com.redpatrimonial.ms_usuarios.dtos.PerfilDocenteDTO;
import com.redpatrimonial.ms_usuarios.dtos.PerfilGuiaDTO;
import com.redpatrimonial.ms_usuarios.exceptions.RecursoNoEncontradoException;
import com.redpatrimonial.ms_usuarios.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_usuarios.models.Docente;
import com.redpatrimonial.ms_usuarios.models.GuiaVoluntario;
import com.redpatrimonial.ms_usuarios.models.Usuario;
import com.redpatrimonial.ms_usuarios.repositories.DocenteRepository;
import com.redpatrimonial.ms_usuarios.repositories.GuiaVoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerfilServiceImpl implements PerfilService {

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private GuiaVoluntarioRepository guiaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public List<Docente> obtenerDocentes() {
        return docenteRepository.findAll();
    }

    @Override
    public Docente obtenerDocente(Long idUsuario) {
        return docenteRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario " + idUsuario + " no tiene perfil de docente."));
    }

    // Crea o actualiza el perfil; solo se permite a usuarios con rol DOCENTE.
    // Es transaccional para que el usuario y su perfil se guarden en la misma sesión de JPA (@MapsId).
    @Override
    @Transactional
    public Docente guardarDocente(Long idUsuario, PerfilDocenteDTO dto) {
        Usuario usuario = validarRol(idUsuario, "DOCENTE");
        Docente docente = docenteRepository.findById(idUsuario).orElseGet(Docente::new);
        docente.setUsuario(usuario);
        docente.setEstablecimiento(dto.getEstablecimiento());
        docente.setAsignatura(dto.getAsignatura());
        return docenteRepository.save(docente);
    }

    @Override
    public List<GuiaVoluntario> obtenerGuias() {
        return guiaRepository.findAll();
    }

    @Override
    public GuiaVoluntario obtenerGuia(Long idUsuario) {
        return guiaRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario " + idUsuario + " no tiene perfil de guía."));
    }

    @Override
    @Transactional
    public GuiaVoluntario guardarGuia(Long idUsuario, PerfilGuiaDTO dto) {
        Usuario usuario = validarRol(idUsuario, "GUIA");
        GuiaVoluntario guia = guiaRepository.findById(idUsuario).orElseGet(GuiaVoluntario::new);
        guia.setUsuario(usuario);
        guia.setEspecialidad(dto.getEspecialidad());
        guia.setIdiomas(dto.getIdiomas());
        guia.setBiografia(dto.getBiografia());
        return guiaRepository.save(guia);
    }

    private Usuario validarRol(Long idUsuario, String rolEsperado) {
        Usuario usuario = usuarioService.obtenerPorId(idUsuario);
        if (!rolEsperado.equals(usuario.getRol().getNombre())) {
            throw new ReglaNegocioException("El usuario " + idUsuario + " no tiene el rol " + rolEsperado + ".");
        }
        return usuario;
    }
}
