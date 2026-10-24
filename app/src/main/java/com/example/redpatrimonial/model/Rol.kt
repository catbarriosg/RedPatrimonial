package com.example.redpatrimonial.model

/**
 * Roles de los usuarios con sesión (actores del documento de casos de uso).
 * El visitante no tiene rol porque usa la app sin iniciar sesión.
 * Según el rol, el login lleva a un panel distinto (ver Routes.panelSegunRol).
 * @property etiqueta nombre del rol para mostrar en pantalla.
 */
enum class Rol(val etiqueta: String) {
    DOCENTE("Docente"),
    ENCARGADO("Encargado de templo"),
    GUIA("Guía voluntario"),
    ADMIN("Administrador de red")
}
