package com.example.redpatrimonial.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

/**
 * Validaciones compartidas por los formularios de la app.
 * Usan java.time (LocalDate y LocalTime): si el texto no tiene el formato correcto, parse() lanza
 * DateTimeParseException, que se captura con try/catch para devolver false en vez de cerrar la app.
 */
object Validaciones {

    /**
     * true si el texto es una fecha real con formato AAAA-MM-DD, por ejemplo 2026-11-20.
     * Rechaza fechas que no existen, como 2026-02-30.
     */
    fun esFechaValida(texto: String): Boolean {
        return try {
            LocalDate.parse(texto.trim())
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    /**
     * true si la fecha es válida y es hoy o posterior. No se puede programar algo en el pasado.
     */
    fun esFechaFutura(texto: String): Boolean {
        return esFechaValida(texto) && !LocalDate.parse(texto.trim()).isBefore(LocalDate.now())
    }

    /**
     * true si el texto es una hora de 24 horas con formato HH:MM, por ejemplo 09:30.
     */
    fun esHoraValida(texto: String): Boolean {
        return try {
            LocalTime.parse(texto.trim())
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    /**
     * true si ambas horas son válidas y la de inicio es anterior a la de término.
     */
    fun esRangoHorarioValido(inicio: String, fin: String): Boolean {
        return esHoraValida(inicio) && esHoraValida(fin) &&
                LocalTime.parse(inicio.trim()).isBefore(LocalTime.parse(fin.trim()))
    }
}
