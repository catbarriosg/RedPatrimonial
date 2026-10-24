package com.redpatrimonial.ms_rutas.controllers;

import com.redpatrimonial.ms_rutas.dtos.SalidaDTO;
import com.redpatrimonial.ms_rutas.models.SalidaPedagogica;
import com.redpatrimonial.ms_rutas.services.SalidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salidas")
@Tag(name = "Salidas pedagógicas", description = "Planificación de visitas de colegios")
public class SalidaController {

    @Autowired
    private SalidaService service;

    @GetMapping
    @Operation(summary = "Salidas de un docente")
    public ResponseEntity<List<SalidaPedagogica>> listar(@RequestParam Long idDocente) {
        return ResponseEntity.ok(service.listarPorDocente(idDocente));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener salida por ID")
    public ResponseEntity<SalidaPedagogica> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Planificar salida y, si se pide, solicitar guía (CU-12, CU-13)")
    public ResponseEntity<SalidaPedagogica> planificar(@Valid @RequestBody SalidaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.planificar(dto));
    }
}
