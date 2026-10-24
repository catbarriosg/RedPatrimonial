package com.example.redpatrimonial.model

/**
 * Estados posibles de un recinto (CU-04 y CU-17).
 * Un enum limita el valor a estas tres opciones y evita errores de tipeo con Strings.
 * Room lo guarda en la base como texto ("ABIERTO", "CERRADO", ...) gracias a su conversor de enums.
 * @property etiqueta texto que se muestra al usuario, con tildes y espacios.
 */
enum class EstadoApertura(val etiqueta: String) {
    ABIERTO("Abierto"),
    CERRADO("Cerrado"),
    EN_CELEBRACION("En celebración")
}
