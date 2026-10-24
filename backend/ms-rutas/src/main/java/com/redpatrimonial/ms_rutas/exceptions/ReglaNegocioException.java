package com.redpatrimonial.ms_rutas.exceptions;

// Datos válidos en formato pero que no cumplen una regla del negocio
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
