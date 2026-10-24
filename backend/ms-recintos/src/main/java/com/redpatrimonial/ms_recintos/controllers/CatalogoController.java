package com.redpatrimonial.ms_recintos.controllers;

import com.redpatrimonial.ms_recintos.models.Comuna;
import com.redpatrimonial.ms_recintos.models.TipoRecinto;
import com.redpatrimonial.ms_recintos.services.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Catálogos", description = "Comunas y tipos de recinto")
public class CatalogoController {

    @Autowired
    private CatalogoService service;

    @GetMapping("/comunas")
    @Operation(summary = "Listar comunas")
    public ResponseEntity<List<Comuna>> listarComunas() {
        return ResponseEntity.ok(service.obtenerComunas());
    }

    @PostMapping("/comunas")
    @Operation(summary = "Registrar comuna")
    public ResponseEntity<Comuna> crearComuna(@Valid @RequestBody Comuna comuna) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearComuna(comuna));
    }

    @GetMapping("/tipos-recinto")
    @Operation(summary = "Listar tipos de recinto")
    public ResponseEntity<List<TipoRecinto>> listarTipos() {
        return ResponseEntity.ok(service.obtenerTipos());
    }

    @PostMapping("/tipos-recinto")
    @Operation(summary = "Registrar tipo de recinto")
    public ResponseEntity<TipoRecinto> crearTipo(@Valid @RequestBody TipoRecinto tipo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearTipo(tipo));
    }
}
