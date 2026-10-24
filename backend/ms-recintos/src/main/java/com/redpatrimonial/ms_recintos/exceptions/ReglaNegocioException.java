package com.redpatrimonial.ms_recintos.exceptions;

// Datos válidos en formato pero que no cumplen una regla del negocio
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
