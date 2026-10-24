package com.example.redpatrimonial.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.ui.theme.*

/**
 * Título de pantalla, con subtítulo y botón para volver opcionales, y acciones a la derecha.
 * - onVolver es una función nullable: si es null no se muestra la flecha.
 * - acciones es un slot: un espacio donde cada pantalla pone sus propios botones (por ejemplo, cerrar sesión).
 *   RowScope permite usar modificadores de Row, como weight, dentro de ese espacio.
 */
@Composable
fun Encabezado(
    titulo: String,
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onVolver != null) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = VerdePatrimonial
            )
            if (subtitulo != null) {
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
            }
        }
        acciones()
    }
}

/**
 * Título pequeño para separar secciones dentro de una pantalla.
 */
@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

/**
 * Etiqueta de color según el estado del recinto: verde abierto, rojo cerrado, ámbar en celebración.
 * "val (fondo, texto) = ..." desestructura el par (Pair) que devuelve el when.
 */
@Composable
fun EstadoChip(estado: EstadoApertura) {
    val (fondo, texto) = when (estado) {
        EstadoApertura.ABIERTO -> FondoExito to VerdeExito
        EstadoApertura.CERRADO -> FondoError to RojoError
        EstadoApertura.EN_CELEBRACION -> FondoAviso to NaranjaAviso
    }
    Etiqueta(estado.etiqueta, fondo = fondo, colorTexto = texto)
}

/**
 * Texto con fondo redondeado (píldora). Base de EstadoChip y de las etiquetas de tipo de evento.
 * clip(RoundedCornerShape(50)) redondea por completo los bordes; el orden de los modificadores importa:
 * primero se recorta, después se pinta el fondo y al final el padding queda por dentro.
 */
@Composable
fun Etiqueta(
    texto: String,
    fondo: androidx.compose.ui.graphics.Color = DoradoClaro,
    colorTexto: androidx.compose.ui.graphics.Color = CafePatrimonial
) {
    Text(
        texto,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = colorTexto,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    )
}

/**
 * Reemplaza a las fotografías mientras no haya recursos multimedia reales.
 * Dibuja un degradado verde con un ícono dorado y un texto.
 */
@Composable
fun ImagenPlaceholder(
    texto: String,
    modifier: Modifier = Modifier,
    icono: ImageVector = Icons.Outlined.Church
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(VerdePatrimonial, VerdeSalvia))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, contentDescription = null, tint = DoradoAntiguo, modifier = Modifier.size(32.dp))
            Text(texto, color = Marfil, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/**
 * Tarjeta de un recinto para listas: imagen, tipo y siglo, nombre, comuna o dirección, y estado.
 * detalleExtra permite mostrar la dirección en vez de la comuna ("?:" usa la comuna si viene null).
 */
@Composable
fun TarjetaRecinto(
    recinto: Recinto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    detalleExtra: String? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenPlaceholder(texto = recinto.tipo, modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${recinto.tipo.uppercase()} · SIGLO ${recinto.siglo}",
                    fontSize = 11.sp,
                    color = Terracota,
                    fontWeight = FontWeight.SemiBold
                )
                Text(recinto.nombre, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(14.dp))
                    Text(
                        detalleExtra ?: recinto.comuna,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                EstadoChip(recinto.estadoActual)
            }
        }
    }
}

/**
 * Texto gris para cuando una lista no tiene elementos.
 */
@Composable
fun MensajeVacio(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        style = MaterialTheme.typography.bodyMedium,
        color = TextoSecundario,
        modifier = modifier.padding(vertical = 16.dp)
    )
}

/**
 * Tarjeta de menú para los paneles de cada rol: ícono, título y descripción.
 * onClick va como último parámetro para poder escribirlo como lambda fuera de los paréntesis.
 */
@Composable
fun TarjetaAccion(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VerdeClaro),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = VerdePatrimonial)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(titulo, fontWeight = FontWeight.SemiBold)
                Text(descripcion, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            }
        }
    }
}

/**
 * Muestra un error de validación debajo de un formulario. Si el texto es null no dibuja nada.
 */
@Composable
fun MensajeError(texto: String?) {
    if (texto != null) {
        Text(texto, color = RojoError, style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * Campo de texto con el estilo de la app, usado en todos los formularios.
 * - valor y onCambio siguen el patrón de Compose: el campo no guarda el texto, lo recibe del ViewModel
 *   y avisa cada cambio para que el ViewModel lo actualice (state hoisting).
 * - ejemplo se muestra en gris mientras el campo está vacío.
 * - lineas > 1 permite texto de varias líneas.
 * - teclado elige el tipo de teclado (numérico, decimal, correo).
 */
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    ejemplo: String? = null,
    lineas: Int = 1,
    teclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        placeholder = if (ejemplo != null) ({ Text(ejemplo, color = TextoSecundario) }) else null,
        singleLine = lineas == 1,
        minLines = lineas,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Se muestra en las pantallas privadas si no hay sesión iniciada; ofrece ir al login.
 */
@Composable
fun SinSesion(onIniciarSesion: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Necesitas iniciar sesión para ver esta sección.", color = TextoSecundario)
        Spacer(modifier = Modifier.height(16.dp))
        BotonPrincipal(texto = "Iniciar sesión", onClick = onIniciarSesion)
    }
}

/**
 * Botón verde principal de los formularios. enabled = false lo desactiva (por ejemplo, mientras carga).
 */
@Composable
fun BotonPrincipal(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = VerdePatrimonial, contentColor = Blanco)
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * Botón dorado para acciones secundarias destacadas, como en el mockup.
 */
@Composable
fun BotonDorado(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DoradoAntiguo, contentColor = TextoPrincipal)
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold)
    }
}
