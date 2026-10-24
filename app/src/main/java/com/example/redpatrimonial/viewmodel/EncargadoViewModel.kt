package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.AperturaEspecial
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.model.Evento
import com.example.redpatrimonial.model.HistorialEstado
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.repository.EventoRepository
import com.example.redpatrimonial.repository.RecintoRepository
import com.example.redpatrimonial.util.Validaciones
import kotlinx.coroutines.launch

/**
 * Gestión del recinto por parte de su encargado (CU-16 a CU-19).
 * Lo usan cuatro pantallas: panel, editar ficha, cambiar estado y publicar evento.
 * Cada pantalla crea su propia instancia, porque viewModel() da una por cada pantalla de la navegación.
 *
 * Los campos de formulario tienen set público para que la pantalla los modifique directamente;
 * mensajeError y mensajeExito solo los cambia el ViewModel.
 */
class EncargadoViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = DatabaseProvider.obtenerBaseDatos(application)
    private val recintoRepository = RecintoRepository(baseDatos.recintoDao())
    private val eventoRepository = EventoRepository(baseDatos.eventoDao())

    var recinto by mutableStateOf<Recinto?>(null)
        private set
    var historial by mutableStateOf<List<HistorialEstado>>(emptyList())
        private set
    var aperturas by mutableStateOf<List<AperturaEspecial>>(emptyList())
        private set

    // Formulario: editar ficha
    var nombre by mutableStateOf("")
    var direccion by mutableStateOf("")
    var resena by mutableStateOf("")
    var horarioSemana by mutableStateOf("")
    var horarioSabado by mutableStateOf("")
    var horarioDomingo by mutableStateOf("")

    // Formulario: cambiar estado y apertura especial
    var estadoElegido by mutableStateOf(EstadoApertura.ABIERTO)
    var aperturaFecha by mutableStateOf("")
    var aperturaInicio by mutableStateOf("")
    var aperturaFin by mutableStateOf("")
    var aperturaMotivo by mutableStateOf("")

    // Formulario: publicar evento
    val tiposEvento = listOf("Liturgia", "Concierto", "Exposición", "Tour cultural", "Aviso")
    var eventoTitulo by mutableStateOf("")
    var eventoTipo by mutableStateOf("Liturgia")
    var eventoFecha by mutableStateOf("")
    var eventoHora by mutableStateOf("")
    var eventoDescripcion by mutableStateOf("")

    var mensajeError by mutableStateOf<String?>(null)
        private set
    var mensajeExito by mutableStateOf<String?>(null)
        private set

    private var cargado = false

    /**
     * Busca el recinto del encargado y lo mantiene actualizado.
     * La bandera cargado evita repetir la carga si la pantalla se recompone.
     * La primera vez que llega el recinto se llenan los formularios y se cargan historial y aperturas;
     * después solo se actualiza el recinto, para no borrar lo que el usuario está escribiendo.
     */
    fun cargar(idEncargado: Int) {
        if (cargado) return
        cargado = true
        viewModelScope.launch {
            recintoRepository.obtenerPorEncargado(idEncargado).collect { encontrado ->
                val primeraCarga = recinto == null && encontrado != null
                recinto = encontrado
                if (primeraCarga) {
                    llenarFormulario(encontrado)
                    cargarListas(encontrado.id)
                }
            }
        }
    }

    /**
     * Copia los datos actuales del recinto en los campos del formulario.
     */
    private fun llenarFormulario(recinto: Recinto) {
        nombre = recinto.nombre
        direccion = recinto.direccion
        resena = recinto.resenaHistorica
        horarioSemana = recinto.horarioSemana
        horarioSabado = recinto.horarioSabado
        horarioDomingo = recinto.horarioDomingo
        estadoElegido = recinto.estadoActual
    }

    /**
     * Empieza a observar el historial de estados y las aperturas especiales del recinto.
     */
    private fun cargarListas(idRecinto: Int) {
        viewModelScope.launch {
            recintoRepository.listarHistorial(idRecinto).collect { historial = it }
        }
        viewModelScope.launch {
            recintoRepository.listarAperturas(idRecinto).collect { aperturas = it }
        }
    }

    /**
     * CU-16: valida y guarda la ficha. Devuelve true si se guardó, para que la pantalla vuelva atrás.
     * recinto.copy(...) crea un Recinto igual al actual pero con los campos editados.
     */
    fun guardarFicha(): Boolean {
        val actual = recinto ?: return false
        if (nombre.isBlank() || direccion.isBlank() || resena.isBlank()) {
            mensajeError = "Nombre, dirección y reseña son obligatorios"
            return false
        }
        viewModelScope.launch {
            recintoRepository.actualizar(
                actual.copy(
                    nombre = nombre.trim(),
                    direccion = direccion.trim(),
                    resenaHistorica = resena.trim(),
                    horarioSemana = horarioSemana.trim(),
                    horarioSabado = horarioSabado.trim(),
                    horarioDomingo = horarioDomingo.trim()
                )
            )
        }
        mensajeError = null
        return true
    }

    /**
     * CU-17: guarda el estado elegido. Si es igual al actual, muestra un error en vez de guardar.
     */
    fun guardarEstado() {
        val actual = recinto ?: return
        if (actual.estadoActual == estadoElegido) {
            mensajeError = "El recinto ya está ${estadoElegido.etiqueta.lowercase()}"
            mensajeExito = null
            return
        }
        viewModelScope.launch {
            recintoRepository.cambiarEstado(actual, estadoElegido)
        }
        mensajeError = null
        mensajeExito = "Estado actualizado a ${estadoElegido.etiqueta}"
    }

    /**
     * CU-18: valida y guarda una apertura especial.
     * when revisa las condiciones en orden y se queda con la primera que se cumple:
     * fecha inválida o pasada, horas inválidas, motivo vacío o, si todo está bien, guarda y limpia el formulario.
     */
    fun programarApertura() {
        val actual = recinto ?: return
        mensajeExito = null
        when {
            !Validaciones.esFechaFutura(aperturaFecha) ->
                mensajeError = "La fecha debe tener el formato AAAA-MM-DD y no puede estar en el pasado"
            !Validaciones.esRangoHorarioValido(aperturaInicio, aperturaFin) ->
                mensajeError = "Las horas deben tener el formato HH:MM y el inicio debe ser antes del término"
            aperturaMotivo.isBlank() ->
                mensajeError = "Indica el motivo de la apertura especial"
            else -> {
                viewModelScope.launch {
                    recintoRepository.programarApertura(
                        AperturaEspecial(
                            idRecinto = actual.id,
                            fecha = aperturaFecha.trim(),
                            horaInicio = aperturaInicio.trim(),
                            horaFin = aperturaFin.trim(),
                            motivo = aperturaMotivo.trim()
                        )
                    )
                }
                aperturaFecha = ""
                aperturaInicio = ""
                aperturaFin = ""
                aperturaMotivo = ""
                mensajeError = null
                mensajeExito = "Apertura especial programada"
            }
        }
    }

    /**
     * CU-19: valida y publica un evento del recinto. idAutor es el usuario con sesión.
     * Devuelve true si se publicó. El evento aparece de inmediato en la pantalla Eventos
     * porque esa pantalla observa la tabla con un Flow.
     */
    fun publicarEvento(idAutor: Int): Boolean {
        val actual = recinto ?: return false
        when {
            eventoTitulo.isBlank() -> mensajeError = "El título es obligatorio"
            !Validaciones.esFechaFutura(eventoFecha) -> mensajeError = "La fecha debe tener el formato AAAA-MM-DD y no puede estar en el pasado"
            !Validaciones.esHoraValida(eventoHora) -> mensajeError = "La hora debe tener el formato HH:MM"
            eventoDescripcion.isBlank() -> mensajeError = "Agrega una descripción breve"
            else -> {
                viewModelScope.launch {
                    eventoRepository.publicar(
                        Evento(
                            idRecinto = actual.id,
                            titulo = eventoTitulo.trim(),
                            tipo = eventoTipo,
                            descripcion = eventoDescripcion.trim(),
                            fecha = eventoFecha.trim(),
                            hora = eventoHora.trim(),
                            idAutor = idAutor
                        )
                    )
                }
                mensajeError = null
                return true
            }
        }
        return false
    }
}
