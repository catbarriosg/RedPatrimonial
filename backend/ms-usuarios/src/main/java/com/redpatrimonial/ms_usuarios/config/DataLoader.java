package com.redpatrimonial.ms_usuarios.config;

import com.redpatrimonial.ms_usuarios.dtos.PerfilDocenteDTO;
import com.redpatrimonial.ms_usuarios.dtos.PerfilGuiaDTO;
import com.redpatrimonial.ms_usuarios.dtos.RegistroUsuarioDTO;
import com.redpatrimonial.ms_usuarios.models.Rol;
import com.redpatrimonial.ms_usuarios.models.Usuario;
import com.redpatrimonial.ms_usuarios.repositories.RolRepository;
import com.redpatrimonial.ms_usuarios.repositories.UsuarioRepository;
import com.redpatrimonial.ms_usuarios.services.PerfilService;
import com.redpatrimonial.ms_usuarios.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Roles y cuentas de prueba. Los IDs 1 a 6 coinciden con los datos de la app y de ms-recintos.
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PerfilService perfilService;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("[DataLoader] La base ya tiene usuarios, no se cargan datos de prueba");
            return;
        }

        crearRol("DOCENTE", "Planifica salidas pedagógicas y solicita guías");
        crearRol("ENCARGADO", "Administra la información de un recinto");
        crearRol("GUIA", "Guía voluntario que acompaña los recorridos");
        crearRol("ADMIN", "Administrador de la red patrimonial");

        Usuario docente = crearUsuario("Docente", "Demo", "docente@demo.cl", "DOCENTE");
        crearUsuario("Encargado", "San Francisco", "encargado@demo.cl", "ENCARGADO");
        crearUsuario("Encargado", "La Merced", "encargado2@demo.cl", "ENCARGADO");
        Usuario guia1 = crearUsuario("Guía", "Demo Uno", "guia@demo.cl", "GUIA");
        Usuario guia2 = crearUsuario("Guía", "Demo Dos", "guia2@demo.cl", "GUIA");
        crearUsuario("Administrador", "de Red", "admin@demo.cl", "ADMIN");

        PerfilDocenteDTO perfilDocente = new PerfilDocenteDTO();
        perfilDocente.setEstablecimiento("Colegio Ficticio Los Andes");
        perfilDocente.setAsignatura("Historia");
        perfilService.guardarDocente(docente.getId(), perfilDocente);

        crearPerfilGuia(guia1.getId(), "Arte colonial", "Español, inglés");
        crearPerfilGuia(guia2.getId(), "Arquitectura religiosa", "Español");

        log.info("[DataLoader] {} usuarios de prueba creados (contraseña 1234)", usuarioRepository.count());
    }

    private void crearRol(String nombre, String descripcion) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        rolRepository.save(rol);
    }

    private Usuario crearUsuario(String nombre, String apellido, String email, String rol) {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setEmail(email);
        dto.setPassword("1234");
        dto.setRol(rol);
        return usuarioService.registrar(dto);
    }

    private void crearPerfilGuia(Long idUsuario, String especialidad, String idiomas) {
        PerfilGuiaDTO dto = new PerfilGuiaDTO();
        dto.setEspecialidad(especialidad);
        dto.setIdiomas(idiomas);
        perfilService.guardarGuia(idUsuario, dto);
    }
}
