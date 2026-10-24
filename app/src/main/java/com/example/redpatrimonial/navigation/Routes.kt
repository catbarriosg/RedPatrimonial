package com.example.redpatrimonial.navigation

import com.example.redpatrimonial.model.Rol

/**
 * Nombres de todas las pantallas (rutas de navegación) en un solo lugar.
 * Usar constantes evita errores de tipeo al navegar y deja ver todas las pantallas de un vistazo.
 * Las rutas con {llaves} reciben argumentos: "detalle_recinto/{recintoId}" se navega como "detalle_recinto/3".
 * conBarraInferior indica qué pantallas públicas muestran la barra de pestañas.
 */
object Routes {
    const val INICIO = "inicio"
    const val LOGIN = "login"

    // Exploración y gestión del recinto (Persona A)
    const val MAPA = "mapa"
    const val BUSCADOR = "buscador"
    const val DETALLE_RECINTO = "detalle_recinto/{recintoId}"
    const val EVENTOS = "eventos"
    const val DETALLE_EVENTO = "detalle_evento/{eventoId}"
    const val FAVORITOS = "favoritos"
    const val PANEL_ENCARGADO = "encargado_panel"
    const val EDITAR_FICHA = "encargado_editar_ficha"
    const val CAMBIAR_ESTADO = "encargado_cambiar_estado"
    const val PUBLICAR_EVENTO = "encargado_publicar_evento"
    const val PANEL_ADMIN = "admin_panel"
    const val REGISTRAR_NODO = "admin_registrar_nodo"
    const val MODERAR = "admin_moderar"

    // Acceso, rutas, visitas y QR (Persona B)
    const val RUTAS = "rutas"
    const val RUTAS_DE_RECINTO = "rutas_recinto/{recintoId}"
    const val RUTA_DETALLE = "ruta_detalle/{rutaId}"
    const val RUTA_PERSONALIZADA = "ruta_personalizada"
    const val QR = "qr"
    const val PANEL_DOCENTE = "docente_panel"
    const val PLANIFICAR_SALIDA = "docente_planificar_salida"
    const val MIS_SOLICITUDES = "docente_solicitudes"
    const val PANEL_GUIA = "guia_panel"
    const val DISPONIBILIDAD = "guia_disponibilidad"
    const val AGENDA = "guia_agenda"
    const val SOLICITUDES_RECINTO = "encargado_solicitudes"
    const val RUTAS_OFICIALES = "admin_rutas_oficiales"

    // Pantallas públicas que muestran la barra inferior
    val conBarraInferior = setOf(
        MAPA, BUSCADOR, DETALLE_RECINTO, EVENTOS, DETALLE_EVENTO, FAVORITOS,
        RUTAS, RUTAS_DE_RECINTO, RUTA_DETALLE, RUTA_PERSONALIZADA, QR
    )

    /**
     * Arma la ruta real hacia la ficha de un recinto, reemplazando {recintoId} por el número.
     * Ejemplo: detalleRecinto(3) devuelve "detalle_recinto/3".
     */
    fun detalleRecinto(id: Int) = "detalle_recinto/$id"
    /**
     * Ruta real hacia el detalle de un evento, por ejemplo "detalle_evento/2".
     */
    fun detalleEvento(id: Int) = "detalle_evento/$id"
    /**
     * Ruta real hacia las rutas que pasan por un recinto.
     */
    fun rutasDeRecinto(id: Int) = "rutas_recinto/$id"
    /**
     * Ruta real hacia el detalle de una ruta.
     */
    fun rutaDetalle(id: Int) = "ruta_detalle/$id"

    /**
     * Pantalla a la que se va después del login según el rol del usuario.
     * when con un enum obliga a cubrir todos los casos: si se agrega un rol nuevo, no compila hasta agregarlo aquí.
     */
    fun panelSegunRol(rol: Rol): String {
        return when (rol) {
            Rol.DOCENTE -> PANEL_DOCENTE
            Rol.ENCARGADO -> PANEL_ENCARGADO
            Rol.GUIA -> PANEL_GUIA
            Rol.ADMIN -> PANEL_ADMIN
        }
    }
}
