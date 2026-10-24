package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.model.VisitaQr
import com.example.redpatrimonial.repository.RecintoRepository
import com.example.redpatrimonial.repository.VisitaRepository
import com.example.redpatrimonial.util.CodigoQr
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Lectura de códigos QR.
 * idLeido es el recinto recién leído: la pantalla navega a su ficha cuando deja de ser null.
 * ultimoCodigo existe porque la cámara entrega el mismo código muchas veces por segundo:
 * así solo se procesa la primera lectura.
 */
class QrViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = DatabaseProvider.obtenerBaseDatos(application)
    private val recintoRepository = RecintoRepository(baseDatos.recintoDao())
    private val visitaRepository = VisitaRepository(baseDatos.visitaDao())

    val recintos: StateFlow<List<Recinto>> =
        recintoRepository.listar().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visitas: StateFlow<List<VisitaQr>> =
        visitaRepository.listarVisitasQr().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var codigoManual by mutableStateOf("")
    var mensajeError by mutableStateOf<String?>(null)
        private set

    // Recinto recién leído; la pantalla navega a su ficha cuando deja de ser null
    var idLeido by mutableStateOf<Int?>(null)
        private set

    // La cámara entrega el mismo código muchas veces por segundo: solo se procesa el primero
    private var ultimoCodigo: String? = null

    /**
     * Procesa el texto leído por la cámara.
     * 1. Ignora el código si es el mismo de antes o si ya hay uno esperando la navegación.
     * 2. Extrae el id con CodigoQr.leerIdRecinto y revisa que el recinto exista.
     * 3. Registra la visita en Room y guarda idLeido para que la pantalla abra la ficha.
     */
    fun procesarCodigo(texto: String) {
        if (texto == ultimoCodigo || idLeido != null) return
        ultimoCodigo = texto

        val id = CodigoQr.leerIdRecinto(texto)
        val recinto = recintos.value.find { it.id == id }
        if (recinto == null) {
            mensajeError = "El código \"$texto\" no pertenece a ningún recinto de la red"
            return
        }
        mensajeError = null
        viewModelScope.launch {
            visitaRepository.registrarVisitaQr(recinto.id)
        }
        idLeido = recinto.id
    }

    /**
     * Procesa el código escrito a mano (útil en el emulador). Borra ultimoCodigo para permitir repetirlo.
     */
    fun procesarCodigoManual() {
        ultimoCodigo = null
        procesarCodigo(codigoManual)
    }

    /**
     * Limpia el estado después de navegar, para poder leer otro código al volver a esta pantalla.
     */
    fun reiniciar() {
        idLeido = null
        ultimoCodigo = null
        codigoManual = ""
    }
}
