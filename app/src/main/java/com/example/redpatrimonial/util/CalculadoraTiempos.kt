package com.example.redpatrimonial.util

import com.example.redpatrimonial.model.Recinto
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Resultado de calcular un recorrido: distancia y minutos caminando y visitando.
 * duracionTotalMin es una propiedad calculada (get() =): se obtiene sumando, no se guarda.
 */
data class ResumenRecorrido(
    val distanciaKm: Double,
    val minutosCaminando: Int,
    val minutosVisita: Int
) {
    val duracionTotalMin: Int get() = minutosCaminando + minutosVisita
}

/**
 * Calcula distancias y tiempos de un recorrido a pie sin depender de internet (CU-10).
 * Es un object porque no guarda estado: solo agrupa funciones y constantes.
 * Mientras no se use el GPS (Prueba 3), la ubicación del usuario se simula en la Plaza de Armas.
 */
object CalculadoraTiempos {

    // Ubicación simulada del usuario mientras no se usa el GPS: Plaza de Armas
    const val LATITUD_SIMULADA = -33.4378
    const val LONGITUD_SIMULADA = -70.6504

    private const val RADIO_TIERRA_KM = 6371.0
    private const val VELOCIDAD_A_PIE_KMH = 4.5

    // Las calles no van en línea recta: se agrega un 30% a la distancia directa
    private const val FACTOR_CALLES = 1.3
    const val MINUTOS_VISITA_POR_PARADA = 20

    /**
     * Distancia en línea recta entre dos coordenadas, en kilómetros (fórmula de Haversine).
     * La Tierra es una esfera, así que no basta con restar coordenadas: la fórmula usa senos y cosenos
     * de los ángulos (convertidos a radianes con Math.toRadians) y el radio de la Tierra (6371 km).
     */
    fun distanciaKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        return RADIO_TIERRA_KM * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    /**
     * Distancia desde la ubicación simulada del usuario hasta un punto. La usan Eventos y el mapa.
     */
    fun distanciaDesdeUsuarioKm(latitud: Double, longitud: Double): Double {
        return distanciaKm(LATITUD_SIMULADA, LONGITUD_SIMULADA, latitud, longitud)
    }

    /**
     * Calcula el resumen de un recorrido que pasa por las paradas en el orden recibido.
     * 1. Suma la distancia entre cada parada y la siguiente (por eso el for llega hasta size - 1).
     * 2. Multiplica por FACTOR_CALLES (1,3) porque las calles no van en línea recta.
     * 3. Convierte la distancia a minutos caminando a 4,5 km/h: minutos = km / velocidad * 60.
     * 4. Suma 20 minutos de visita por cada parada.
     * roundToInt() redondea al entero más cercano; (x * 10).roundToInt() / 10.0 deja un decimal.
     */
    fun calcular(paradas: List<Recinto>): ResumenRecorrido {
        var distancia = 0.0
        for (i in 0 until paradas.size - 1) {
            val desde = paradas[i]
            val hasta = paradas[i + 1]
            distancia += distanciaKm(desde.latitud, desde.longitud, hasta.latitud, hasta.longitud)
        }
        distancia *= FACTOR_CALLES

        val minutosCaminando = (distancia / VELOCIDAD_A_PIE_KMH * 60).roundToInt()
        return ResumenRecorrido(
            distanciaKm = (distancia * 10).roundToInt() / 10.0,
            minutosCaminando = minutosCaminando,
            minutosVisita = paradas.size * MINUTOS_VISITA_POR_PARADA
        )
    }

    /**
     * Texto para mostrar una distancia: en metros si es menos de 1 km ("350 m"), si no en km con un decimal ("1.7 km").
     */
    fun formatearDistancia(km: Double): String {
        return if (km < 1) "${(km * 1000).roundToInt()} m" else "${(km * 10).roundToInt() / 10.0} km"
    }

    /**
     * Texto para mostrar una duración en minutos: "45 min", "2 h" o "1 h 31 min".
     * Usa división entera (/) para las horas y resto (%) para los minutos que sobran.
     * when elige el formato según el caso, como un if/else if más ordenado.
     */
    fun formatearDuracion(minutos: Int): String {
        val horas = minutos / 60
        val resto = minutos % 60
        return when {
            horas == 0 -> "$resto min"
            resto == 0 -> "${horas} h"
            else -> "${horas} h ${resto} min"
        }
    }
}
