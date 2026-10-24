package com.redpatrimonial.ms_usuarios.services;

import com.redpatrimonial.ms_usuarios.dtos.ActualizarUsuarioDTO;
import com.redpatrimonial.ms_usuarios.dtos.LoginDTO;
import com.redpatrimonial.ms_usuarios.dtos.RegistroUsuarioDTO;
import com.redpatrimonial.ms_usuarios.dtos.SesionDTO;
import com.redpatrimonial.ms_usuarios.exceptions.CredencialesInvalidasException;
import com.redpatrimonial.ms_usuarios.exceptions.RecursoNoEncontradoException;
import com.redpatrimonial.ms_usuarios.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_usuarios.models.Rol;
import com.redpatrimonial.ms_usuarios.models.Usuario;
import com.redpatrimonial.ms_usuarios.repositories.RolRepository;
import com.redpatrimonial.ms_usuarios.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Rol> obtenerRoles() {
        return rolRepository.findAll();
    }

    @Override
    public List<Usuario> obtenerTodos(String rol) {
        if (rol == null || rol.isBlank()) {
            return repository.findByActivoTrueOrderByNombreAsc();
        }
        return repository.findByRolNombreAndActivoTrueOrderByNombreAsc(rol.toUpperCase());
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        return repository.findById(id)
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID " + id + " no encontrado."));
    }

    @Override
    public Usuario registrar(RegistroUsuarioDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) {
            log.warn("[UsuarioService] Correo ya registrado: {}", email);
            throw new ReglaNegocioException("Ya existe una cuenta con el correo " + email + ".");
        }
        Rol rol = rolRepository.findByNombre(dto.getRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol " + dto.getRol() + " no existe."));

        Usuario usuario = new Usuario();
        usuario.setRol(rol);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(email);
        usuario.setTelefono(dto.getTelefono());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        Usuario guardado = repository.save(usuario);
        log.info("[UsuarioService] Usuario {} registrado con rol {} (ID {})", email, rol.getNombre(), guardado.getId());
        return guardado;
    }

    @Override
    public Usuario actualizar(Long id, ActualizarUsuarioDTO dto) {
        Usuario usuario = obtenerPorId(id);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        return repository.save(usuario);
    }

    @Override
    public Usuario desactivar(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setActivo(false);
        log.info("[UsuarioService] Usuario ID {} desactivado", id);
        return repository.save(usuario);
    }

    // CU-15: el mismo mensaje para correo inexistente o clave incorrecta, para no revelar qué cuentas existen
    @Override
    public SesionDTO login(LoginDTO dto) {
        Usuario usuario = repository.findByEmailIgnoreCase(dto.getEmail().trim())
                .filter(Usuario::getActivo)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
            log.warn("[UsuarioService] Contraseña incorrecta para {}", usuario.getEmail());
            throw new CredencialesInvalidasException();
        }

        log.info("[UsuarioService] Inicio de sesión de {} ({})", usuario.getEmail(), usuario.getRol().getNombre());
        return new SesionDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol().getNombre());
    }
}
