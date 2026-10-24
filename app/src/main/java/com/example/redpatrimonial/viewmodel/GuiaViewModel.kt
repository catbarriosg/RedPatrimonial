package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.DisponibilidadGuia
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.model.Rol
import com.example.redpatrimonial.model.SolicitudGuia
import com.example.redpatrimonial.model.Usuario
import com.example.redpatrimonial.repository.RecintoRepository
import com.example.redpatrimonial.repository.UsuarioRepository
import com.example.redpatrimonial.repository.VisitaRepository
import com.example.redpatrimonial.util.Validaciones
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Coordinación de guías:
 * - Vista del guía: CU-21 disponibilidad, CU-22 responder asignaciones y CU-23 agenda.
 * - Vista del encargado: CU-20 asignar un guía a las solicitudes de su recinto.
 * guiaElegido es un mapa observable que guarda, para cada solicitud pendiente, el guía elegido en los chips.
 */
class GuiaViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = DatabaseProvider.obtenerBaseDatos(application)
    private val visitaRepository = VisitaRepository(baseDatos.visitaDao())
    private val recintoRepository = RecintoRepository(baseDatos.recintoDao())
    private val usuarioRepository = UsuarioRepository(baseDatos.usuarioDao())

    val recintos: StateFlow<List<Recinto>> =
        recintoRepository.listar().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val usuarios: StateFlow<List<Usuario>> =
        usuarioRepository.listar().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val guias: StateFlow<List<Usuario>> =
        usuarioRepository.listarPorRol(Rol.GUIA).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    // Vista del guía
    var asignaciones by mutableStateOf<List<SolicitudGuia>>(emptyList())
        private set
    var agenda by mutableStateOf<List<SolicitudGuia>>(emptyList())
        private set
    var disponibilidad by mutableStateOf<List<DisponibilidadGuia>>(emptyList())
        private set

    var diaElegido by mutableStateOf("Lunes")
    var horaInicio by mutableStateOf("")
    var horaFin by mutableStateOf("")

    // Vista del encargado
    var recintoEncargado by mutableStateOf<Recinto?>(null)
        private set
    var solicitudesRecinto by mutableStateOf<List<SolicitudGuia>>(emptyList())
        private set

    // Guía elegido en cada solicitud pendiente (id solicitud -> id guía)
    val guiaElegido = mutableStateMapOf<Int, Int>()

    var mensajeError by mutableStateOf<String?>(null)
        private set

    private var cargado = false

    /**
     * Empieza a observar las asignaciones por responder, la agenda y la disponibilidad del guía.
     */
    fun cargarGuia(idGuia: Int) {
        if (cargado) return
        cargado = true
        viewModelScope.launch {
            visitaRepository.listarAsignacionesGuia(idGuia).collect { asignaciones = it }
        }
        viewModelScope.launch {
            visitaRepository.listarAgendaGuia(idGuia).collect { agenda = it }
        }
        viewModelScope.launch {
            visitaRepository.listarDisponibilidad(idGuia).collect { disponibilidad = it }
        }
    }

    /**
     * El guía acepta o rechaza un recorrido asignado.
     */
    fun responder(solicitud: SolicitudGuia, acepta: Boolean) {
        viewModelScope.launch {
            visitaRepository.responderAsignacion(solicitud, acepta)
        }
    }

    /**
     * Valida las horas y agrega un bloque de disponibilidad para el día elegido.
     */
    fun agregarDisponibilidad(idGuia: Int) {
        if (!Validaciones.esRangoHorarioValido(horaInicio, horaFin)) {
            mensajeError = "Las horas deben tener el formato HH:MM y el inicio debe ser antes del término"
            return
        }
        mensajeError = null
        viewModelScope.launch {
            visitaRepository.agregarDisponibilidad(
                DisponibilidadGuia(
                    idGuia = idGuia,
                    diaSemana = diaElegido,
                    horaInicio = horaInicio.trim(),
                    horaFin = horaFin.trim()
                )
            )
        }
        horaInicio = ""
        horaFin = ""
    }

    /**
     * Elimina un bloque de disponibilidad.
     */
    fun eliminarDisponibilidad(id: Int) {
        viewModelScope.launch {
            visitaRepository.eliminarDisponibilidad(id)
        }
    }

    /**
     * Busca el recinto del encargado y, cuando llega por primera vez, empieza a observar sus solicitudes.
     */
    fun cargarEncargado(idEncargado: Int) {
        if (cargado) return
        cargado = true
        viewModelScope.launch {
            recintoRepository.obtenerPorEncargado(idEncargado).collect { recinto ->
                val primeraCarga = recintoEncargado == null && recinto != null
                recintoEncargado = recinto
                if (primeraCarga) {
                    viewModelScope.launch {
                        visitaRepository.listarSolicitudesRecinto(recinto.id).collect { solicitudesRecinto = it }
                    }
                }
            }
        }
    }

    /**
     * Asigna a la solicitud el guía elegido en los chips. Si no se eligió ninguno, muestra un error.
     */
    fun asignar(idSolicitud: Int) {
        val idGuia = guiaElegido[idSolicitud]
        if (idGuia == null) {
            mensajeError = "Elige un guía antes de asignar"
            return
        }
        mensajeError = null
        viewModelScope.launch {
            visitaRepository.asignarGuia(idSolicitud, idGuia)
        }
    }

    /**
     * El encargado rechaza una solicitud pendiente.
     */
    fun rechazar(idSolicitud: Int) {
        viewModelScope.launch {
            visitaRepository.rechazarSolicitud(idSolicitud)
        }
    }

    /**
     * Nombre de un usuario a partir de su id, o "Sin asignar" si no se encuentra.
     */
    fun nombreUsuario(usuarios: List<Usuario>, id: Int?): String {
        return usuarios.find { it.id == id }?.nombre ?: "Sin asignar"
    }

    /**
     * Nombre de un recinto a partir de su id.
     */
    fun nombreRecinto(recintos: List<Recinto>, id: Int): String {
        return recintos.find { it.id == id }?.nombre ?: "Recinto #$id"
    }
}
