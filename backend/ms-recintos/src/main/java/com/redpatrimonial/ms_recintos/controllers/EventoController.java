package com.redpatrimonial.ms_recintos.controllers;

import com.redpatrimonial.ms_recintos.dtos.EventoDTO;
import com.redpatrimonial.ms_recintos.dtos.ModeracionDTO;
import com.redpatrimonial.ms_recintos.enums.TipoEvento;
import com.redpatrimonial.ms_recintos.models.Evento;
import com.redpatrimonial.ms_recintos.models.Moderacion;
import com.redpatrimonial.ms_recintos.services.EventoService;
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
@RequestMapping("/api/eventos")
@Tag(name = "Eventos", description = "Oferta programática y cultural de los recintos")
public class EventoController {

    private static final Logger log = LoggerFactory.getLogger(EventoController.class);

    @Autowired
    private EventoService service;

    @GetMapping
    @Operation(summary = "Listar eventos visibles, opcionalmente por tipo")
    public ResponseEntity<List<Evento>> listar(@RequestParam(required = false) TipoEvento tipo) {
        log.info("[EventoController] GET /api/eventos tipo={}", tipo);
        return ResponseEntity.ok(service.obtenerVisibles(tipo));
    }

    @GetMapping("/cercanos")
    @Operation(summary = "Eventos cercanos a una ubicación (CU-06)")
    public ResponseEntity<List<Evento>> cercanos(
            @RequestParam double latitud,
            @RequestParam double longitud,
            @RequestParam(defaultValue = "3") double radioKm) {
        return ResponseEntity.ok(service.obtenerCercanos(latitud, longitud, radioKm));
    }

    @GetMapping("/todos")
    @Operation(summary = "Todos los eventos, incluidos los ocultos (moderación)")
    public ResponseEntity<List<Evento>> todos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/recinto/{idRecinto}")
    @Operation(summary = "Eventos visibles de un recinto")
    public ResponseEntity<List<Evento>> porRecinto(@PathVariable Long idRecinto) {
        return ResponseEntity.ok(service.obtenerPorRecinto(idRecinto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener evento por ID")
    public ResponseEntity<Evento> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Publicar evento o aviso (CU-19)")
    public ResponseEntity<Evento> publicar(@Valid @RequestBody EventoDTO dto) {
        log.info("[EventoController] POST /api/eventos - {}", dto.getTitulo());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.publicar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evento")
    public ResponseEntity<Evento> actualizar(@PathVariable Long id, @Valid @RequestBody EventoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evento")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Evento con ID " + id + " eliminado."));
    }

    @PatchMapping("/{id}/moderacion")
    @Operation(summary = "Moderar publicación: aprobar u ocultar (CU-25)")
    public ResponseEntity<Evento> moderar(@PathVariable Long id, @Valid @RequestBody ModeracionDTO dto) {
        log.info("[EventoController] PATCH /api/eventos/{}/moderacion -> {}", id, dto.getAccion());
        return ResponseEntity.ok(service.moderar(id, dto));
    }

    @GetMapping("/{id}/moderacion")
    @Operation(summary = "Historial de moderación de un evento")
    public ResponseEntity<List<Moderacion>> moderaciones(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerModeraciones(id));
    }
}
