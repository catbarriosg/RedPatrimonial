package com.redpatrimonial.ms_recintos.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Errores de validación (@NotBlank, @NotNull, @DecimalMin, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> respuesta = crearRespuesta("VALIDACION_FALLIDA", "Los datos enviados no cumplen con los requisitos");
        respuesta.put("detalles", errores);
        log.warn("[GlobalExceptionHandler] Error de validación: {}", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    // 2. JSON mal formado o un valor de enum que no existe
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(HttpMessageNotReadableException ex) {
        log.warn("[GlobalExceptionHandler] Cuerpo de la petición ilegible: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(crearRespuesta("JSON_INVALIDO", "El cuerpo de la petición no tiene el formato esperado"));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn("[GlobalExceptionHandler] Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearRespuesta("NO_ENCONTRADO", ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleReglaNegocio(ReglaNegocioException ex) {
        log.warn("[GlobalExceptionHandler] Regla de negocio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(crearRespuesta("REGLA_DE_NEGOCIO", ex.getMessage()));
    }

    // 3. Violación de una restricción única en la base de datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicado(DataIntegrityViolationException ex) {
        log.error("[GlobalExceptionHandler] DataIntegrityViolation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(crearRespuesta("CONFLICTO_DE_DATOS", "El registro entra en conflicto con datos existentes"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeExceptions(RuntimeException ex) {
        log.error("[GlobalExceptionHandler] Error no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearRespuesta("ERROR_INTERNO", "Ocurrió un error inesperado en el servidor"));
    }

    private Map<String, Object> crearRespuesta(String estado, String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("estado", estado);
        respuesta.put("mensaje", mensaje);
        return respuesta;
    }
}
