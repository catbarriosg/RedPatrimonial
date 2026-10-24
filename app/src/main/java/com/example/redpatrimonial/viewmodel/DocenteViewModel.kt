package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.ParadaRuta
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.model.Rol
import com.example.redpatrimonial.model.Ruta
import com.example.redpatrimonial.model.SalidaPedagogica
import com.example.redpatrimonial.model.SolicitudGuia
import com.example.redpatrimonial.model.Usuario
import com.example.redpatrimonial.repository.RecintoRepository
import com.example.redpatrimonial.repository.RutaRepository
import com.example.redpatrimonial.repository.UsuarioRepository
import com.example.redpatrimonial.repository.VisitaRepository
import com.example.redpatrimonial.util.Validaciones
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Funciones del docente: planificar salida (CU-12), solicitar guía (CU-13) y ver el estado de sus solicitudes (CU-14).
 * rutas combina las oficiales y las personalizadas en una sola lista con el operador +.
 */
class DocenteViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = DatabaseProvider.obtenerBaseDatos(application)
    private val visitaRepository = VisitaRepository(baseDatos.visitaDao())
    private val rutaRepository = RutaRepository(baseDatos.rutaDao())
    private val recintoRepository = RecintoRepository(baseDatos.recintoDao())
    private val usuarioRepository = UsuarioRepository(baseDatos.usuarioDao())

    val rutas: StateFlow<List<Ruta>> =
        combine(rutaRepository.listarOficiales(), rutaRepository.listarPersonalizadas()) { oficiales, propias ->
            oficiales + propias
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paradas: StateFlow<List<ParadaRuta>> =
        rutaRepository.listarTodasLasParadas().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recintos: StateFlow<List<Recinto>> =
        recintoRepository.listar().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val guias: StateFlow<List<Usuario>> =
        usuarioRepository.listarPorRol(Rol.GUIA).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var salidas by mutableStateOf<List<SalidaPedagogica>>(emptyList())
        private set
    var solicitudes by mutableStateOf<List<SolicitudGuia>>(emptyList())
        private set

    // Formulario: planificar salida
    var idRuta by mutableStateOf<Int?>(null)
    var fecha by mutableStateOf("")
    var hora by mutableStateOf("")
    var tamanoGrupo by mutableStateOf("")
    var nivelEducativo by mutableStateOf("")
    var observaciones by mutableStateOf("")
    var solicitarGuia by mutableStateOf(true)
    var idRecintoGuia by mutableStateOf<Int?>(null)
    var comentario by mutableStateOf("")

    var mensajeError by mutableStateOf<String?>(null)
        private set

    private var cargado = false

    /**
     * Empieza a observar las salidas y solicitudes del docente. La bandera cargado evita repetirlo.
     */
    fun cargar(idDocente: Int) {
        if (cargado) return
        cargado = true
        viewModelScope.launch {
            visitaRepository.listarSalidas(idDocente).collect { salidas = it }
        }
        viewModelScope.launch {
            visitaRepository.listarSolicitudesDocente(idDocente).collect { solicitudes = it }
        }
    }

    /**
     * Elige la ruta de la salida y limpia el recinto del guía, porque la nueva ruta tiene otras paradas.
     */
    fun elegirRuta(id: Int) {
        idRuta = id
        idRecintoGuia = null
    }

    /**
     * Recintos de una ruta en orden. Se usan para elegir en qué recinto se necesita el guía.
     */
    fun recintosDeRuta(idRuta: Int?, paradas: List<ParadaRuta>, recintos: List<Recinto>): List<Recinto> {
        return paradas
            .filter { it.idRuta == idRuta }
            .sortedBy { it.orden }
            .mapNotNull { parada -> recintos.find { it.id == parada.idRecinto } }
    }

    /**
     * Valida el formulario y guarda la salida (y la solicitud de guía si se pidió).
     * Devuelve true si se guardó. Las validaciones van en orden dentro del when y se muestra la primera que falla.
     */
    fun planificar(idDocente: Int): Boolean {
        val grupo = tamanoGrupo.toIntOrNull()
        val ruta = idRuta
        when {
            ruta == null -> mensajeError = "Elige la ruta de la salida"
            !Validaciones.esFechaFutura(fecha) -> mensajeError = "La fecha debe tener el formato AAAA-MM-DD y no puede estar en el pasado"
            !Validaciones.esHoraValida(hora) -> mensajeError = "La hora debe tener el formato HH:MM"
            grupo == null || grupo !in 1..60 -> mensajeError = "El grupo debe tener entre 1 y 60 estudiantes"
            nivelEducativo.isBlank() -> mensajeError = "Indica el nivel educativo, por ejemplo 2° medio"
            solicitarGuia && idRecintoGuia == null -> mensajeError = "Elige en qué recinto necesitas el guía"
            else -> {
                val salida = SalidaPedagogica(
                    idDocente = idDocente,
                    idRuta = ruta,
                    fecha = fecha.trim(),
                    hora = hora.trim(),
                    tamanoGrupo = grupo,
                    nivelEducativo = nivelEducativo.trim(),
                    observaciones = observaciones.trim()
                )
                viewModelScope.launch {
                    visitaRepository.planificarSalida(salida, solicitarGuia, idRecintoGuia ?: 0, comentario.trim())
                }
                mensajeError = null
                return true
            }
        }
        return false
    }
}
