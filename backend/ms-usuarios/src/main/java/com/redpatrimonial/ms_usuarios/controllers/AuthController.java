package com.redpatrimonial.ms_usuarios.controllers;

import com.redpatrimonial.ms_usuarios.dtos.LoginDTO;
import com.redpatrimonial.ms_usuarios.dtos.SesionDTO;
import com.redpatrimonial.ms_usuarios.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Inicio de sesión de los usuarios de la red")
public class AuthController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión (CU-15)")
    public ResponseEntity<SesionDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(service.login(dto));
    }
}
