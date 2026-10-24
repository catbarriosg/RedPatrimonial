package com.redpatrimonial.ms_recintos.controllers;

import com.redpatrimonial.ms_recintos.dtos.CambioEstadoDTO;
import com.redpatrimonial.ms_recintos.dtos.RecintoDTO;
import com.redpatrimonial.ms_recintos.enums.EstadoApertura;
import com.redpatrimonial.ms_recintos.models.*;
import com.redpatrimonial.ms_recintos.services.RecintoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/recintos")
@Tag(name = "Recintos", description = "Fichas patrimoniales, estados de apertura y horarios")
public class RecintoController {

    private static final Logger log = LoggerFactory.getLogger(RecintoController.class);

    @Autowired
    private RecintoService service;

    @GetMapping
    @Operation(summary = "Listar y filtrar recintos activos (CU-01, CU-02)")
    public ResponseEntity<List<Recinto>> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long idComuna,
            @RequestParam(required = false) Long idTipo,
            @RequestParam(required = false) EstadoApertura estado) {
        log.info("[RecintoController] GET /api/recintos");
        return ResponseEntity.ok(service.buscar(texto, idComuna, idTipo, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ficha patrimonial (CU-03)")
    public ResponseEntity<Recinto> obtener(@PathVariable Long id) {
        log.info("[RecintoController] GET /api/recintos/{}", id);
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/encargado/{idEncargado}")
    @Operation(summary = "Recintos a cargo de un encargado")
    public ResponseEntity<List<Recinto>> listarPorEncargado(@PathVariable Long idEncargado) {
        return ResponseEntity.ok(service.obtenerPorEncargado(idEncargado));
    }

    @PostMapping
    @Operation(summary = "Registrar nodo de la red (CU-24)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recinto registrado"),
            @ApiResponse(responseCode = "400", description = "Validación fallida")
    })
    public ResponseEntity<Recinto> registrar(@Valid @RequestBody RecintoDTO dto) {
        log.info("[RecintoController] POST /api/recintos - {}", dto.getNombreOficial());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ficha patrimonial (CU-16)")
    public ResponseEntity<Recinto> actualizar(@PathVariable Long id, @Valid @RequestBody RecintoDTO dto) {
        log.info("[RecintoController] PUT /api/recintos/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de apertura (CU-17)")
    public ResponseEntity<Recinto> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoDTO dto) {
        log.info("[RecintoController] PATCH /api/recintos/{}/estado -> {}", id, dto.getEstado());
        return ResponseEntity.ok(service.cambiarEstado(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar recinto (soft-delete)")
    public ResponseEntity<Map<String, String>> desactivar(@PathVariable Long id) {
        Recinto recinto = service.desactivar(id);
        return ResponseEntity.ok(Map.of("mensaje", "El recinto '" + recinto.getNombreOficial() + "' fue desactivado."));
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Historial de cambios de estado")
    public ResponseEntity<List<HistorialEstado>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerHistorial(id));
    }

    @GetMapping("/{id}/aperturas")
    @Operation(summary = "Listar aperturas especiales (CU-04)")
    public ResponseEntity<List<AperturaEspecial>> aperturas(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerAperturas(id));
    }

    @PostMapping("/{id}/aperturas")
    @Operation(summary = "Programar apertura especial (CU-18)")
    public ResponseEntity<AperturaEspecial> programarApertura(@PathVariable Long id, @Valid @RequestBody AperturaEspecial apertura) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.programarApertura(id, apertura));
    }

    @GetMapping("/{id}/horarios")
    @Operation(summary = "Horarios regulares de visita (CU-04)")
    public ResponseEntity<List<Horario>> horarios(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerHorarios(id));
    }

    @PostMapping("/{id}/horarios")
    @Operation(summary = "Agregar horario regular")
    public ResponseEntity<Horario> agregarHorario(@PathVariable Long id, @Valid @RequestBody Horario horario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarHorario(id, horario));
    }

    @GetMapping("/{id}/multimedia")
    @Operation(summary = "Galería multimedia del recinto")
    public ResponseEntity<List<Multimedia>> multimedia(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerMultimedia(id));
    }

    @PostMapping("/{id}/multimedia")
    @Operation(summary = "Agregar recurso multimedia")
    public ResponseEntity<Multimedia> agregarMultimedia(@PathVariable Long id, @Valid @RequestBody Multimedia multimedia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarMultimedia(id, multimedia));
    }
}
