package com.redpatrimonial.ms_usuarios.controllers;

import com.redpatrimonial.ms_usuarios.dtos.ActualizarUsuarioDTO;
import com.redpatrimonial.ms_usuarios.dtos.RegistroUsuarioDTO;
import com.redpatrimonial.ms_usuarios.models.Rol;
import com.redpatrimonial.ms_usuarios.models.Usuario;
import com.redpatrimonial.ms_usuarios.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Usuarios", description = "Cuentas y roles de la red")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService service;

    @GetMapping("/roles")
    @Operation(summary = "Listar roles")
    public ResponseEntity<List<Rol>> roles() {
        return ResponseEntity.ok(service.obtenerRoles());
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuarios activos, opcionalmente por rol")
    public ResponseEntity<List<Usuario>> listar(@RequestParam(required = false) String rol) {
        log.info("[UsuarioController] GET /api/usuarios rol={}", rol);
        return ResponseEntity.ok(service.obtenerTodos(rol));
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping("/usuarios")
    @Operation(summary = "Registrar usuario")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroUsuarioDTO dto) {
        log.info("[UsuarioController] POST /api/usuarios - {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Actualizar datos de la cuenta")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarUsuarioDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Desactivar usuario (soft-delete)")
    public ResponseEntity<Map<String, String>> desactivar(@PathVariable Long id) {
        Usuario usuario = service.desactivar(id);
        return ResponseEntity.ok(Map.of("mensaje", "La cuenta " + usuario.getEmail() + " fue desactivada."));
    }
}
