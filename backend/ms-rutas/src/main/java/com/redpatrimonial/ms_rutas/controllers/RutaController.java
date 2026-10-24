package com.redpatrimonial.ms_rutas.controllers;

import com.redpatrimonial.ms_rutas.dtos.RutaDTO;
import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import com.redpatrimonial.ms_rutas.models.Ruta;
import com.redpatrimonial.ms_rutas.services.RutaService;
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
@RequestMapping("/api/rutas")
@Tag(name = "Rutas", description = "Rutas patrimoniales oficiales y personalizadas")
public class RutaController {

    private static final Logger log = LoggerFactory.getLogger(RutaController.class);

    @Autowired
    private RutaService service;

    @GetMapping
    @Operation(summary = "Listar rutas por tipo, por recinto incluido o por usuario (CU-08)")
    public ResponseEntity<List<Ruta>> listar(
            @RequestParam(required = false) TipoRuta tipo,
            @RequestParam(required = false) Long idRecinto,
            @RequestParam(required = false) Long idUsuario) {
        log.info("[RutaController] GET /api/rutas tipo={} recinto={} usuario={}", tipo, idRecinto, idUsuario);
        return ResponseEntity.ok(service.listar(tipo, idRecinto, idUsuario));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ruta con sus paradas")
    public ResponseEntity<Ruta> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear ruta oficial (CU-26) o personalizada (CU-09)")
    public ResponseEntity<Ruta> crear(@Valid @RequestBody RutaDTO dto) {
        log.info("[RutaController] POST /api/rutas - {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ruta y sus paradas")
    public ResponseEntity<Ruta> actualizar(@PathVariable Long id, @Valid @RequestBody RutaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ruta")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Ruta con ID " + id + " eliminada."));
    }
}
