
package com.example.redpatrimonial.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.EstadoChip
import com.example.redpatrimonial.ui.components.ImagenPlaceholder
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.util.CalculadoraTiempos
import com.example.redpatrimonial.viewmodel.FavoritoViewModel
import com.example.redpatrimonial.viewmodel.MapaViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

// Límites del área que muestra el mapa (centro de Santiago, Recoleta y Providencia)
private const val LAT_NORTE = -33.420
private const val LAT_SUR = -33.458
private const val LNG_OESTE = -70.670
private const val LNG_ESTE = -70.610

private val ColorPapel = Color(0xFFEFE6D2)
private val ColorRio = Color(0xFF9DB8C7)
private val ColorParque = Color(0xFFB9CDA8)

/**
 * Pantalla del mapa (CU-01): buscador, mapa con los recintos, leyenda y tarjeta del recinto tocado.
 *
 * Conceptos de Compose que aparecen aquí:
 * - Una función @Composable describe la interfaz; Compose la vuelve a ejecutar (recompone)
 *   cuando cambia un estado que lee.
 * - viewModel() entrega el ViewModel de esta pantalla; si ya existe (por ejemplo, tras rotar) devuelve el mismo.
 *   Va como parámetro con valor por defecto para poder reemplazarlo en pruebas.
 * - collectAsState() convierte el StateFlow en estado de Compose: cuando Room cambia, la pantalla se redibuja.
 * - "by" permite usar recintos directamente en vez de recintos.value.
 * - weight(1f) hace que el mapa ocupe todo el alto que sobra entre los demás elementos.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    mapaViewModel: MapaViewModel = viewModel(),
    favoritoViewModel: FavoritoViewModel = viewModel()
) {
    val recintos by mapaViewModel.recintos.collectAsState()
    val favoritos by favoritoViewModel.favoritos.collectAsState()
    val seleccionado = recintos.find { it.id == mapaViewModel.idSeleccionado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BarraSuperiorMapa(
            onBuscar = { navController.navigate(Routes.BUSCADOR) },
            onFavoritos = { navController.navigate(Routes.FAVORITOS) },
            onCuenta = {
                val usuario = sesionViewModel.usuario
                navController.navigate(if (usuario != null) Routes.panelSegunRol(usuario.rol) else Routes.LOGIN)
            }
        )

        MapaPatrimonial(
            recintos = recintos,
            idSeleccionado = mapaViewModel.idSeleccionado,
            onSeleccionar = { mapaViewModel.seleccionar(it) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        LeyendaEstados()

        if (seleccionado != null) {
            val esFavorito = favoritoViewModel.esRecintoFavorito(favoritos, seleccionado.id)
            TarjetaSeleccionado(
                recinto = seleccionado,
                esFavorito = esFavorito,
                onFavorito = { favoritoViewModel.alternarRecinto(seleccionado.id, esFavorito) },
                onVerFicha = { navController.navigate(Routes.detalleRecinto(seleccionado.id)) }
            )
        } else {
            Text(
                "Toca un marcador para ver el recinto. Hay ${recintos.size} recintos en la red.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }
    }
}

/**
 * Fila superior del mapa: una caja que parece buscador (al tocarla abre la pantalla Buscador),
 * el botón de favoritos y el botón de cuenta.
 */
@Composable
private fun BarraSuperiorMapa(onBuscar: () -> Unit, onFavoritos: () -> Unit, onCuenta: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(FondoTarjeta)
                .clickable(onClick = onBuscar)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = DoradoAntiguo)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Buscar templos…", color = TextoSecundario, fontSize = 14.sp, maxLines = 1)
        }
        IconButton(onClick = onFavoritos) {
            Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Mis favoritos", tint = VerdePatrimonial)
        }
        IconButton(onClick = onCuenta) {
            Icon(Icons.Outlined.AccountCircle, contentDescription = "Mi cuenta", tint = VerdePatrimonial)
        }
    }
}

/**
 * Convierte una coordenada GPS en una posición (x, y) dentro del mapa dibujado.
 * Es una regla de tres: qué fracción del ancho y del alto ocupa la coordenada entre los límites del mapa.
 * La y se calcula desde LAT_NORTE porque en la pantalla y crece hacia abajo, y la latitud crece hacia el norte.
 */
private fun aPunto(latitud: Double, longitud: Double, ancho: Float, alto: Float): Offset {
    val x = ((longitud - LNG_OESTE) / (LNG_ESTE - LNG_OESTE)).toFloat() * ancho
    val y = ((LAT_NORTE - latitud) / (LAT_NORTE - LAT_SUR)).toFloat() * alto
    return Offset(x, y)
}

/**
 * Color del marcador según el estado del recinto.
 */
private fun colorEstado(estado: EstadoApertura): Color {
    return when (estado) {
        EstadoApertura.ABIERTO -> VerdeExito
        EstadoApertura.CERRADO -> RojoError
        EstadoApertura.EN_CELEBRACION -> NaranjaAviso
    }
}

/**
 * Dibuja el mapa del centro de Santiago y pone un marcador por recinto.
 * - BoxWithConstraints entrega el tamaño disponible (maxWidth, maxHeight) para ubicar los marcadores.
 * - Canvas permite dibujar a mano: líneas de calles, el río Mapocho y la Alameda (Path), el cerro y la plaza (círculos).
 * - rememberTextMeasurer() mide textos para dibujarlos dentro del Canvas.
 * - LocalDensity convierte dp (unidades de diseño) a píxeles con toPx().
 * - Cada marcador es un Icon desplazado con offset a su posición; al tocarlo llama a onSeleccionar.
 * En la Prueba 3 este dibujo se reemplaza por un mapa real sin cambiar los datos.
 */
@Composable
private fun MapaPatrimonial(
    recintos: List<Recinto>,
    idSeleccionado: Int?,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val densidad = LocalDensity.current
    val estiloEtiqueta = TextStyle(fontSize = 10.sp, color = CafePatrimonial, fontWeight = FontWeight.Medium)

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ColorPapel)
    ) {
        val anchoPx = with(densidad) { maxWidth.toPx() }
        val altoPx = with(densidad) { maxHeight.toPx() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Calles: una grilla suave como en el damero fundacional
            val separacion = 28.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(BordeSuave, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += separacion
            }
            var y = 0f
            while (y < size.height) {
                drawLine(BordeSuave, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                y += separacion
            }

            // Río Mapocho
            val rio = listOf(
                -33.4300 to -70.670, -33.4335 to -70.656, -33.4330 to -70.645,
                -33.4318 to -70.636, -33.4285 to -70.625, -33.4245 to -70.610
            ).map { (lat, lng) -> aPunto(lat, lng, size.width, size.height) }
            val caminoRio = Path().apply {
                moveTo(rio.first().x, rio.first().y)
                rio.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(caminoRio, ColorRio, style = Stroke(width = 9.dp.toPx()))
            drawText(textMeasurer, "Río Mapocho", rio[1] + Offset(0f, -18.dp.toPx()), estiloEtiqueta)

            // Alameda
            val alameda = listOf(
                -33.4442 to -70.670, -33.4412 to -70.640, -33.4330 to -70.625, -33.4262 to -70.610
            ).map { (lat, lng) -> aPunto(lat, lng, size.width, size.height) }
            val caminoAlameda = Path().apply {
                moveTo(alameda.first().x, alameda.first().y)
                alameda.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(caminoAlameda, Blanco, style = Stroke(width = 6.dp.toPx()))
            drawText(textMeasurer, "Alameda", alameda.first() + Offset(8.dp.toPx(), 6.dp.toPx()), estiloEtiqueta)

            // Cerro Santa Lucía
            val cerro = aPunto(-33.4405, -70.6440, size.width, size.height)
            drawCircle(ColorParque, radius = 14.dp.toPx(), center = cerro)
            drawText(textMeasurer, "Cerro Santa Lucía", cerro + Offset(16.dp.toPx(), -4.dp.toPx()), estiloEtiqueta)

            // Plaza de Armas y ubicación simulada del usuario
            val plaza = aPunto(CalculadoraTiempos.LATITUD_SIMULADA, CalculadoraTiempos.LONGITUD_SIMULADA, size.width, size.height)
            drawCircle(Color(0x332F6FDB), radius = 16.dp.toPx(), center = plaza)
            drawCircle(Blanco, radius = 7.dp.toPx(), center = plaza)
            drawCircle(Color(0xFF2F6FDB), radius = 5.dp.toPx(), center = plaza)
            drawText(textMeasurer, "Tú · Plaza de Armas", plaza + Offset(-36.dp.toPx(), 10.dp.toPx()), estiloEtiqueta)
        }

        // Marcadores de cada recinto
        recintos.forEach { recinto ->
            val punto = aPunto(recinto.latitud, recinto.longitud, anchoPx, altoPx)
            val seleccionado = recinto.id == idSeleccionado
            val tamano = if (seleccionado) 40.dp else 30.dp
            val tamanoPx = with(densidad) { tamano.toPx() }

            Box(
                modifier = Modifier.offset {
                    IntOffset((punto.x - tamanoPx / 2).toInt(), (punto.y - tamanoPx).toInt())
                }
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = recinto.nombre,
                    tint = colorEstado(recinto.estadoActual),
                    modifier = Modifier
                        .size(tamano)
                        .clickable { onSeleccionar(recinto.id) }
                )
            }
            if (seleccionado) {
                Text(
                    recinto.nombre,
                    fontSize = 11.sp,
                    color = Blanco,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .offset { IntOffset((punto.x - tamanoPx).toInt(), (punto.y - tamanoPx * 1.9f).toInt()) }
                        .clip(RoundedCornerShape(8.dp))
                        .background(VerdePatrimonial)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Leyenda de colores de los marcadores. EstadoApertura.entries recorre todos los valores del enum.
 */
@Composable
private fun LeyendaEstados() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        EstadoApertura.entries.forEach { estado ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colorEstado(estado))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(estado.etiqueta, fontSize = 12.sp, color = TextoSecundario)
            }
        }
    }
}

/**
 * Tarjeta del recinto tocado en el mapa: datos, distancia desde el usuario, botón de favorito y botón para ver la ficha.
 */
@Composable
private fun TarjetaSeleccionado(
    recinto: Recinto,
    esFavorito: Boolean,
    onFavorito: () -> Unit,
    onVerFicha: () -> Unit
) {
    val distancia = CalculadoraTiempos.distanciaDesdeUsuarioKm(recinto.latitud, recinto.longitud)

    Card(
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ImagenPlaceholder(texto = recinto.tipo, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${recinto.tipo.uppercase()} · SIGLO ${recinto.siglo}",
                        fontSize = 11.sp,
                        color = Terracota,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(recinto.nombre, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${recinto.direccion} · A ${CalculadoraTiempos.formatearDistancia(distancia)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                        maxLines = 2
                    )
                }
                EstadoChip(recinto.estadoActual)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onFavorito,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Terracota,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (esFavorito) "Guardado" else "Favorito", color = TextoPrincipal)
                }
                Button(
                    onClick = onVerFicha,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePatrimonial)
                ) {
                    Text("Ver ficha completa")
                }
            }
        }
    }
}
