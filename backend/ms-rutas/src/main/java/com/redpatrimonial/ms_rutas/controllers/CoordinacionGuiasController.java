package com.redpatrimonial.ms_rutas.controllers;

import com.redpatrimonial.ms_rutas.dtos.AsignacionDTO;
import com.redpatrimonial.ms_rutas.dtos.RespuestaDTO;
import com.redpatrimonial.ms_rutas.enums.EstadoSolicitud;
import com.redpatrimonial.ms_rutas.models.AsignacionRecorrido;
import com.redpatrimonial.ms_rutas.models.DisponibilidadGuia;
import com.redpatrimonial.ms_rutas.models.SolicitudGuia;
import com.redpatrimonial.ms_rutas.services.CoordinacionGuiasService;
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
@Tag(name = "Coordinación de guías", description = "Solicitudes, asignaciones, disponibilidad y agenda")
public class CoordinacionGuiasController {

    private static final Logger log = LoggerFactory.getLogger(CoordinacionGuiasController.class);

    @Autowired
    private CoordinacionGuiasService service;

    @GetMapping("/solicitudes")
    @Operation(summary = "Solicitudes por docente (CU-14), por recinto (CU-20) o por estado")
    public ResponseEntity<List<SolicitudGuia>> solicitudes(
            @RequestParam(required = false) Long idDocente,
            @RequestParam(required = false) Long idRecinto,
            @RequestParam(required = false) EstadoSolicitud estado) {
        return ResponseEntity.ok(service.listarSolicitudes(idDocente, idRecinto, estado));
    }

    @GetMapping("/solicitudes/{id}")
    @Operation(summary = "Obtener solicitud por ID")
    public ResponseEntity<SolicitudGuia> solicitud(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerSolicitud(id));
    }

    @PatchMapping("/solicitudes/{id}/rechazo")
    @Operation(summary = "Rechazar una solicitud pendiente")
    public ResponseEntity<SolicitudGuia> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(service.rechazarSolicitud(id));
    }

    @PostMapping("/asignaciones")
    @Operation(summary = "Asignar guía a una solicitud (CU-20)")
    public ResponseEntity<AsignacionRecorrido> asignar(@Valid @RequestBody AsignacionDTO dto) {
        log.info("[CoordinacionGuiasController] POST /api/asignaciones solicitud={} guía={}", dto.getIdSolicitud(), dto.getIdGuia());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.asignarGuia(dto));
    }

    @PatchMapping("/asignaciones/{id}/respuesta")
    @Operation(summary = "El guía acepta o rechaza el recorrido (CU-22)")
    public ResponseEntity<AsignacionRecorrido> responder(@PathVariable Long id, @RequestBody RespuestaDTO dto) {
        return ResponseEntity.ok(service.responder(id, dto));
    }

    @GetMapping("/guias/{idGuia}/asignaciones")
    @Operation(summary = "Asignaciones de un guía")
    public ResponseEntity<List<AsignacionRecorrido>> asignaciones(@PathVariable Long idGuia) {
        return ResponseEntity.ok(service.listarAsignaciones(idGuia));
    }

    @GetMapping("/guias/{idGuia}/agenda")
    @Operation(summary = "Recorridos confirmados del guía (CU-23)")
    public ResponseEntity<List<AsignacionRecorrido>> agenda(@PathVariable Long idGuia) {
        return ResponseEntity.ok(service.obtenerAgenda(idGuia));
    }

    @GetMapping("/guias/{idGuia}/disponibilidad")
    @Operation(summary = "Disponibilidad semanal del guía")
    public ResponseEntity<List<DisponibilidadGuia>> disponibilidad(@PathVariable Long idGuia) {
        return ResponseEntity.ok(service.listarDisponibilidad(idGuia));
    }

    @PostMapping("/guias/{idGuia}/disponibilidad")
    @Operation(summary = "Registrar bloque de disponibilidad (CU-21)")
    public ResponseEntity<DisponibilidadGuia> agregarDisponibilidad(@PathVariable Long idGuia, @Valid @RequestBody DisponibilidadGuia disponibilidad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarDisponibilidad(idGuia, disponibilidad));
    }

    @DeleteMapping("/disponibilidad/{id}")
    @Operation(summary = "Eliminar bloque de disponibilidad")
    public ResponseEntity<Map<String, String>> eliminarDisponibilidad(@PathVariable Long id) {
        service.eliminarDisponibilidad(id);
        return ResponseEntity.ok(Map.of("mensaje", "Bloque de disponibilidad eliminado."));
    }
}
