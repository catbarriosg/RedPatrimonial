package com.example.redpatrimonial

import com.example.redpatrimonial.util.CodigoQr
import com.example.redpatrimonial.util.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ValidacionesTest {

    @Test
    fun fechasConFormatoAAAAMMDD() {
        assertTrue(Validaciones.esFechaValida("2026-11-20"))
        assertFalse(Validaciones.esFechaValida("20-11-2026"))
        assertFalse(Validaciones.esFechaValida("2026-02-30"))
        assertFalse(Validaciones.esFechaValida(""))
    }

    @Test
    fun unaFechaPasadaNoEsFutura() {
        assertFalse(Validaciones.esFechaFutura(LocalDate.now().minusDays(1).toString()))
        assertTrue(Validaciones.esFechaFutura(LocalDate.now().toString()))
    }

    @Test
    fun rangoHorarioExigeInicioAntesDelTermino() {
        assertTrue(Validaciones.esRangoHorarioValido("09:00", "13:00"))
        assertFalse(Validaciones.esRangoHorarioValido("13:00", "09:00"))
        assertFalse(Validaciones.esRangoHorarioValido("9", "13:00"))
    }

    @Test
    fun codigoQrDeLaRed() {
        assertEquals("redpatrimonial://recinto/7", CodigoQr.contenidoRecinto(7))
        assertEquals(7, CodigoQr.leerIdRecinto("redpatrimonial://recinto/7"))
        assertEquals(3, CodigoQr.leerIdRecinto(" 3 "))
        assertNull(CodigoQr.leerIdRecinto("https://ejemplo.cl"))
    }
}
