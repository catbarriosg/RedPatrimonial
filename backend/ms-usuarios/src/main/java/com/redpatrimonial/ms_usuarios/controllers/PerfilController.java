package com.redpatrimonial.ms_usuarios.controllers;

import com.redpatrimonial.ms_usuarios.dtos.PerfilDocenteDTO;
import com.redpatrimonial.ms_usuarios.dtos.PerfilGuiaDTO;
import com.redpatrimonial.ms_usuarios.models.Docente;
import com.redpatrimonial.ms_usuarios.models.GuiaVoluntario;
import com.redpatrimonial.ms_usuarios.services.PerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Perfiles", description = "Datos adicionales de docentes y guías voluntarios")
public class PerfilController {

    @Autowired
    private PerfilService service;

    @GetMapping("/docentes")
    @Operation(summary = "Listar perfiles de docentes")
    public ResponseEntity<List<Docente>> docentes() {
        return ResponseEntity.ok(service.obtenerDocentes());
    }

    @GetMapping("/docentes/{idUsuario}")
    @Operation(summary = "Obtener perfil de docente")
    public ResponseEntity<Docente> docente(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.obtenerDocente(idUsuario));
    }

    @PutMapping("/docentes/{idUsuario}")
    @Operation(summary = "Crear o actualizar perfil de docente")
    public ResponseEntity<Docente> guardarDocente(@PathVariable Long idUsuario, @Valid @RequestBody PerfilDocenteDTO dto) {
        return ResponseEntity.ok(service.guardarDocente(idUsuario, dto));
    }

    @GetMapping("/guias")
    @Operation(summary = "Listar perfiles de guías voluntarios")
    public ResponseEntity<List<GuiaVoluntario>> guias() {
        return ResponseEntity.ok(service.obtenerGuias());
    }

    @GetMapping("/guias/{idUsuario}")
    @Operation(summary = "Obtener perfil de guía")
    public ResponseEntity<GuiaVoluntario> guia(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.obtenerGuia(idUsuario));
    }

    @PutMapping("/guias/{idUsuario}")
    @Operation(summary = "Crear o actualizar perfil de guía")
    public ResponseEntity<GuiaVoluntario> guardarGuia(@PathVariable Long idUsuario, @Valid @RequestBody PerfilGuiaDTO dto) {
        return ResponseEntity.ok(service.guardarGuia(idUsuario, dto));
    }
}
