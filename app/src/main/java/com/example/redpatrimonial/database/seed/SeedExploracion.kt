package com.example.redpatrimonial.database.seed

import com.example.redpatrimonial.database.AppDatabase
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.model.Evento
import com.example.redpatrimonial.model.Recinto

/**
 * Catálogo sintético de recintos y eventos (Persona A). Los datos son públicos o ficticios.
 * Los IDs de los recintos (1 a 10) son fijos porque las rutas y solicitudes de prueba los usan.
 * La lista recintos es pública porque SeedRutasVisitas la usa para calcular distancias.
 */
object SeedExploracion {

    // IDs de usuarios definidos en SeedRutasVisitas
    private const val ID_ENCARGADO_SAN_FRANCISCO = 2
    private const val ID_ENCARGADO_MERCED = 3

    val recintos = listOf(
        Recinto(
            id = 1,
            nombre = "Iglesia de San Francisco",
            tipo = "Iglesia",
            comuna = "Santiago",
            direccion = "Av. Libertador Bernardo O'Higgins 834, Santiago Centro",
            latitud = -33.4440,
            longitud = -70.6476,
            categoria = "Monumento Nacional",
            estilo = "Colonial",
            siglo = "XVI",
            resenaHistorica = "Inaugurada en 1618, es la construcción más antigua de Santiago y de todo Chile. " +
                    "Ha resistido numerosos terremotos gracias a sus robustos muros de piedra y alberga " +
                    "un valioso conjunto de arte sacro del periodo colonial.",
            horarioSemana = "09:00 - 18:00",
            horarioSabado = "10:00 - 14:00",
            horarioDomingo = "11:00 - 13:00",
            estadoActual = EstadoApertura.ABIERTO,
            idEncargado = ID_ENCARGADO_SAN_FRANCISCO
        ),
        Recinto(
            id = 2,
            nombre = "Catedral Metropolitana",
            tipo = "Catedral",
            comuna = "Santiago",
            direccion = "Plaza de Armas s/n, Santiago Centro",
            latitud = -33.4378,
            longitud = -70.6510,
            categoria = "Monumento Nacional",
            estilo = "Neoclásico",
            siglo = "XVIII",
            resenaHistorica = "Sede del Arzobispado de Santiago, frente a la Plaza de Armas. El edificio actual " +
                    "se levantó tras sucesivos terremotos y combina trazas coloniales con una fachada " +
                    "neoclásica terminada a fines del siglo XIX.",
            horarioSemana = "10:00 - 19:00",
            horarioSabado = "10:00 - 18:00",
            horarioDomingo = "09:00 - 13:00",
            estadoActual = EstadoApertura.EN_CELEBRACION
        ),
        Recinto(
            id = 3,
            nombre = "Iglesia de Santo Domingo",
            tipo = "Iglesia",
            comuna = "Santiago",
            direccion = "Santo Domingo 961, Santiago Centro",
            latitud = -33.4352,
            longitud = -70.6487,
            categoria = "Monumento Nacional",
            estilo = "Neoclásico",
            siglo = "XVIII",
            resenaHistorica = "Templo de la orden dominica construido en piedra canteada del cerro Blanco. " +
                    "Su fachada de dos torres es uno de los ejemplos más tempranos del neoclásico en Santiago.",
            horarioSemana = "10:00 - 17:00",
            horarioSabado = "10:00 - 13:00",
            horarioDomingo = "10:00 - 12:30",
            estadoActual = EstadoApertura.CERRADO
        ),
        Recinto(
            id = 4,
            nombre = "Basílica de la Merced",
            tipo = "Basílica",
            comuna = "Santiago",
            direccion = "Mac-Iver 341, Santiago Centro",
            latitud = -33.4370,
            longitud = -70.6467,
            categoria = "Monumento Nacional",
            estilo = "Neorrenacentista",
            siglo = "XVIII",
            resenaHistorica = "Iglesia de la orden mercedaria, presente en Santiago desde la fundación de la ciudad. " +
                    "Conserva un púlpito y un museo con piezas de arte colonial e insular.",
            horarioSemana = "09:30 - 18:30",
            horarioSabado = "10:00 - 14:00",
            horarioDomingo = "10:00 - 13:00",
            estadoActual = EstadoApertura.ABIERTO,
            idEncargado = ID_ENCARGADO_MERCED
        ),
        Recinto(
            id = 5,
            nombre = "Basílica de los Sacramentinos",
            tipo = "Basílica",
            comuna = "Santiago",
            direccion = "Arturo Prat 471, Santiago Centro",
            latitud = -33.4530,
            longitud = -70.6490,
            categoria = "Monumento Nacional",
            estilo = "Neorrománico",
            siglo = "XX",
            resenaHistorica = "Inspirada en el Sagrado Corazón de Montmartre, en París. Su cripta y su cúpula " +
                    "la convierten en uno de los hitos del barrio San Diego.",
            horarioSemana = "10:00 - 18:00",
            horarioSabado = "10:00 - 13:00",
            horarioDomingo = "10:00 - 13:00",
            estadoActual = EstadoApertura.ABIERTO
        ),
        Recinto(
            id = 6,
            nombre = "Basílica del Salvador",
            tipo = "Basílica",
            comuna = "Santiago",
            direccion = "Huérfanos 1781, Barrio Brasil",
            latitud = -33.4415,
            longitud = -70.6643,
            categoria = "Monumento Nacional",
            estilo = "Neogótico",
            siglo = "XIX",
            resenaHistorica = "Gran templo neogótico de ladrillo en el Barrio Brasil. Dañada por los terremotos " +
                    "de 1985 y 2010, es un símbolo de la recuperación del patrimonio de la ciudad.",
            horarioSemana = "Cerrado por restauración",
            horarioSabado = "Cerrado por restauración",
            horarioDomingo = "Cerrado por restauración",
            estadoActual = EstadoApertura.CERRADO
        ),
        Recinto(
            id = 7,
            nombre = "Capilla de la Veracruz",
            tipo = "Capilla",
            comuna = "Santiago",
            direccion = "José Victorino Lastarria 124, Barrio Lastarria",
            latitud = -33.4385,
            longitud = -70.6413,
            categoria = "Monumento Nacional",
            estilo = "Neogótico",
            siglo = "XIX",
            resenaHistorica = "Pequeña capilla de fachada neogótica en el corazón del Barrio Lastarria, " +
                    "levantada a mediados del siglo XIX.",
            horarioSemana = "11:00 - 14:00",
            horarioSabado = "11:00 - 13:00",
            horarioDomingo = "10:00 - 12:00",
            estadoActual = EstadoApertura.ABIERTO
        ),
        Recinto(
            id = 8,
            nombre = "Basílica de la Recoleta Dominica",
            tipo = "Basílica",
            comuna = "Recoleta",
            direccion = "Av. Recoleta 759, Recoleta",
            latitud = -33.4270,
            longitud = -70.6440,
            categoria = "Monumento Nacional",
            estilo = "Neoclásico",
            siglo = "XIX",
            resenaHistorica = "Templo de columnas de mármol al norte del río Mapocho. Su antiguo claustro " +
                    "acoge hoy un museo de artes decorativas y de historia dominica.",
            horarioSemana = "10:00 - 17:00",
            horarioSabado = "10:00 - 14:00",
            horarioDomingo = "10:00 - 13:00",
            estadoActual = EstadoApertura.ABIERTO
        ),
        Recinto(
            id = 9,
            nombre = "Santuario de la Divina Providencia",
            tipo = "Santuario",
            comuna = "Providencia",
            direccion = "Av. Providencia 1990, Providencia",
            latitud = -33.4290,
            longitud = -70.6178,
            categoria = "Monumento Nacional",
            estilo = "Neorrománico",
            siglo = "XIX",
            resenaHistorica = "Iglesia de dos torres rojas que dio nombre a la comuna. Fue parte del " +
                    "complejo de las Hermanas de la Providencia, dedicado a la acogida de niños.",
            horarioSemana = "08:00 - 20:00",
            horarioSabado = "09:00 - 13:00",
            horarioDomingo = "09:00 - 13:00",
            estadoActual = EstadoApertura.ABIERTO
        ),
        Recinto(
            id = 10,
            nombre = "Museo de Arte Colonial San Francisco",
            tipo = "Museo",
            comuna = "Santiago",
            direccion = "Londres 4, Barrio París-Londres",
            latitud = -33.4446,
            longitud = -70.6470,
            categoria = "Museo",
            estilo = "Colonial",
            siglo = "XVI",
            resenaHistorica = "Funciona en el antiguo convento franciscano. Reúne pinturas, platería y " +
                    "objetos litúrgicos de la época colonial en torno a un claustro con jardín.",
            horarioSemana = "10:00 - 18:00",
            horarioSabado = "10:00 - 14:00",
            horarioDomingo = "Cerrado",
            estadoActual = EstadoApertura.ABIERTO
        )
    )

    private val eventos = listOf(
        Evento(
            idRecinto = 1,
            titulo = "Misa Coral de San Francisco",
            tipo = "Liturgia",
            descripcion = "Celebración dominical acompañada por el coro de la parroquia.",
            fecha = "2026-11-22",
            hora = "11:00"
        ),
        Evento(
            idRecinto = 2,
            titulo = "Concierto de Órgano Barroco",
            tipo = "Concierto",
            descripcion = "Obras de Bach y Buxtehude interpretadas en el órgano de la Catedral.",
            fecha = "2026-11-27",
            hora = "19:30"
        ),
        Evento(
            idRecinto = 5,
            titulo = "Visita Nocturna Guiada",
            tipo = "Tour cultural",
            descripcion = "Recorrido por la cripta y la nave central con iluminación especial.",
            fecha = "2026-11-28",
            hora = "20:30"
        ),
        Evento(
            idRecinto = 10,
            titulo = "Exposición: Platería del Virreinato",
            tipo = "Exposición",
            descripcion = "Muestra temporal de piezas de platería litúrgica de los siglos XVII y XVIII.",
            fecha = "2026-11-05",
            hora = "10:00"
        ),
        Evento(
            idRecinto = 8,
            titulo = "Apertura especial del claustro",
            tipo = "Aviso",
            descripcion = "El claustro abrirá excepcionalmente para delegaciones escolares.",
            fecha = "2026-11-12",
            hora = "15:00"
        ),
        Evento(
            idRecinto = 4,
            titulo = "Ciclo de música sacra",
            tipo = "Concierto",
            descripcion = "Cuarteto de cuerdas con repertorio de música sacra colonial.",
            fecha = "2026-12-03",
            hora = "19:00"
        )
    )

    /**
     * Inserta los recintos y después los eventos (los eventos necesitan que sus recintos existan).
     */
    suspend fun poblar(baseDatos: AppDatabase) {
        baseDatos.recintoDao().recintoAlmacenarTodos(recintos)
        baseDatos.eventoDao().eventoAlmacenarTodos(eventos)
    }
}
