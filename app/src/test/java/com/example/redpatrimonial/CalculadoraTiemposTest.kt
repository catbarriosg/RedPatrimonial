package com.example.redpatrimonial

import com.example.redpatrimonial.database.seed.SeedExploracion
import com.example.redpatrimonial.util.CalculadoraTiempos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculadoraTiemposTest {

    private fun recinto(id: Int) = SeedExploracion.recintos.first { it.id == id }

    @Test
    fun distanciaEntreUnPuntoYSiMismoEsCero() {
        assertEquals(0.0, CalculadoraTiempos.distanciaKm(-33.44, -70.65, -33.44, -70.65), 0.0001)
    }

    @Test
    fun distanciaEntreCatedralYMercedEsMenorAMedioKilometro() {
        val catedral = recinto(2)
        val merced = recinto(4)
        val km = CalculadoraTiempos.distanciaKm(catedral.latitud, catedral.longitud, merced.latitud, merced.longitud)
        assertTrue("Se esperaba menos de 0,5 km y se obtuvo $km", km < 0.5)
    }

    @Test
    fun unaSolaParadaSoloSumaElTiempoDeVisita() {
        val resumen = CalculadoraTiempos.calcular(listOf(recinto(1)))
        assertEquals(0, resumen.minutosCaminando)
        assertEquals(CalculadoraTiempos.MINUTOS_VISITA_POR_PARADA, resumen.duracionTotalMin)
    }

    @Test
    fun rutaDeTresBasilicasCoincideConElBackend() {
        // ms-rutas guarda 31 + 33 minutos de traslado para esta misma ruta
        val resumen = CalculadoraTiempos.calcular(listOf(recinto(4), recinto(5), recinto(6)))
        assertEquals(64, resumen.minutosCaminando)
        assertEquals(60, resumen.minutosVisita)
        assertEquals(4.8, resumen.distanciaKm, 0.01)
    }

    @Test
    fun formatosDeDistanciaYDuracion() {
        assertEquals("350 m", CalculadoraTiempos.formatearDistancia(0.35))
        assertEquals("1.7 km", CalculadoraTiempos.formatearDistancia(1.68))
        assertEquals("45 min", CalculadoraTiempos.formatearDuracion(45))
        assertEquals("2 h", CalculadoraTiempos.formatearDuracion(120))
        assertEquals("1 h 31 min", CalculadoraTiempos.formatearDuracion(91))
    }
}
