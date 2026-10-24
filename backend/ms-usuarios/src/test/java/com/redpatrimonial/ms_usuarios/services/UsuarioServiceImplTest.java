package com.redpatrimonial.ms_usuarios.services;

import com.redpatrimonial.ms_usuarios.dtos.LoginDTO;
import com.redpatrimonial.ms_usuarios.dtos.RegistroUsuarioDTO;
import com.redpatrimonial.ms_usuarios.exceptions.CredencialesInvalidasException;
import com.redpatrimonial.ms_usuarios.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_usuarios.models.Rol;
import com.redpatrimonial.ms_usuarios.models.Usuario;
import com.redpatrimonial.ms_usuarios.repositories.RolRepository;
import com.redpatrimonial.ms_usuarios.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioServiceImpl service;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void configurar() {
        ReflectionTestUtils.setField(service, "passwordEncoder", encoder);
    }

    private Usuario usuario(boolean activo) {
        Rol rol = new Rol();
        rol.setNombre("DOCENTE");
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Docente");
        usuario.setEmail("docente@demo.cl");
        usuario.setPasswordHash(encoder.encode("1234"));
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return usuario;
    }

    private LoginDTO login(String password) {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("docente@demo.cl");
        dto.setPassword(password);
        return dto;
    }

    @Test
    void loginCorrectoDevuelveRol() {
        when(repository.findByEmailIgnoreCase("docente@demo.cl")).thenReturn(Optional.of(usuario(true)));

        assertEquals("DOCENTE", service.login(login("1234")).getRol());
    }

    @Test
    void loginConClaveIncorrectaFalla() {
        when(repository.findByEmailIgnoreCase("docente@demo.cl")).thenReturn(Optional.of(usuario(true)));

        assertThrows(CredencialesInvalidasException.class, () -> service.login(login("9999")));
    }

    @Test
    void usuarioDesactivadoNoPuedeIniciarSesion() {
        when(repository.findByEmailIgnoreCase("docente@demo.cl")).thenReturn(Optional.of(usuario(false)));

        assertThrows(CredencialesInvalidasException.class, () -> service.login(login("1234")));
    }

    @Test
    void registrarGuardaLaClaveCifrada() {
        Rol rol = new Rol();
        rol.setNombre("GUIA");
        when(repository.existsByEmailIgnoreCase("nuevo@demo.cl")).thenReturn(false);
        when(rolRepository.findByNombre("GUIA")).thenReturn(Optional.of(rol));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setNombre("Nuevo");
        dto.setEmail("Nuevo@Demo.cl");
        dto.setPassword("secreta");
        dto.setRol("GUIA");

        Usuario guardado = service.registrar(dto);

        assertEquals("nuevo@demo.cl", guardado.getEmail());
        assertNotEquals("secreta", guardado.getPasswordHash());
        assertTrue(encoder.matches("secreta", guardado.getPasswordHash()));
    }

    @Test
    void correoDuplicadoNoSeRegistra() {
        when(repository.existsByEmailIgnoreCase("docente@demo.cl")).thenReturn(true);

        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setNombre("Otro");
        dto.setEmail("docente@demo.cl");
        dto.setPassword("1234");
        dto.setRol("DOCENTE");

        assertThrows(ReglaNegocioException.class, () -> service.registrar(dto));
        verify(repository, never()).save(any());
    }
}
