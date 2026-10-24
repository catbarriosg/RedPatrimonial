package com.redpatrimonial.ms_usuarios.exceptions;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Correo o contraseña incorrectos.");
    }
}
