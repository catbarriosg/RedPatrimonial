package com.example.redpatrimonial.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.viewmodel.QrViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

/**
 * Lectura de códigos QR con la cámara.
 * 1. Revisa si ya tiene permiso de cámara; si no, lo pide al abrir la pantalla.
 *    rememberLauncherForActivityResult registra la petición de permiso y recibe la respuesta (true o false).
 * 2. Si hay permiso muestra la cámara; si no, un botón para volver a pedirlo.
 * 3. Cuando el ViewModel detecta un código válido (idLeido), LaunchedEffect navega a la ficha
 *    y reinicia el lector.
 * También permite escribir el código a mano y muestra las últimas visitas registradas.
 */
@Composable
fun QrScannerScreen(
    navController: NavController,
    qrViewModel: QrViewModel = viewModel()
) {
    val contexto = LocalContext.current
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        tienePermiso = concedido
    }
    LaunchedEffect(Unit) {
        if (!tienePermiso) pedirPermiso.launch(Manifest.permission.CAMERA)
    }

    val recintos by qrViewModel.recintos.collectAsState()
    val visitas by qrViewModel.visitas.collectAsState()
    val formatoFecha = remember { SimpleDateFormat("dd-MM HH:mm", Locale.getDefault()) }

    // Al leer un código válido se abre la ficha del recinto
    val idLeido = qrViewModel.idLeido
    LaunchedEffect(idLeido) {
        if (idLeido != null) {
            navController.navigate(Routes.detalleRecinto(idLeido))
            qrViewModel.reiniciar()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Escanear código QR",
            subtitulo = "Apunta la cámara al código en la entrada del recinto"
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(BorderStroke(3.dp, DoradoAntiguo), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (tienePermiso) {
                    VistaCamara(
                        onCodigo = { qrViewModel.procesarCodigo(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("La app necesita permiso para usar la cámara y leer el código.", color = TextoSecundario)
                        Spacer(modifier = Modifier.height(12.dp))
                        BotonPrincipal(texto = "Dar permiso", onClick = { pedirPermiso.launch(Manifest.permission.CAMERA) })
                    }
                }
            }

            MensajeError(qrViewModel.mensajeError)

            // Alternativa para probar en el emulador, donde no hay un código real que enfocar
            Text("¿No puedes escanear? Escribe el código o el número del recinto", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoTexto(
                    valor = qrViewModel.codigoManual,
                    onCambio = { qrViewModel.codigoManual = it },
                    etiqueta = "Código",
                    ejemplo = "redpatrimonial://recinto/1",
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { qrViewModel.procesarCodigoManual() },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePatrimonial)
                ) {
                    Text("Abrir")
                }
            }

            TituloSeccion("Últimas visitas registradas")
            if (visitas.isEmpty()) {
                MensajeVacio("Aún no escaneas ningún recinto.")
            }
            visitas.take(5).forEach { visita ->
                val nombre = recintos.find { it.id == visita.idRecinto }?.nombre ?: "Recinto #${visita.idRecinto}"
                Text("${formatoFecha.format(Date(visita.fechaHora))} · $nombre", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/**
 * Vista previa de CameraX que entrega cada imagen al lector de códigos de ML Kit.
 * - CameraX es una librería de Android; su PreviewView es una vista clásica, por eso se envuelve con AndroidView.
 * - ProcessCameraProvider controla la cámara. bindToLifecycle la enciende y la apaga sola junto con la pantalla.
 * - Preview muestra la imagen; ImageAnalysis entrega cada cuadro para analizarlo en un hilo aparte (ejecutor).
 * - STRATEGY_KEEP_ONLY_LATEST descarta los cuadros viejos si el análisis va más lento que la cámara.
 * - DisposableEffect libera el hilo y el lector de ML Kit cuando la pantalla se cierra.
 */
@Composable
private fun VistaCamara(onCodigo: (String) -> Unit, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val ejecutor = remember { Executors.newSingleThreadExecutor() }
    val escaner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            ejecutor.shutdown()
            escaner.close()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { contexto ->
            val previewView = PreviewView(contexto)
            val proveedorFuturo = ProcessCameraProvider.getInstance(contexto)
            proveedorFuturo.addListener({
                val proveedor = proveedorFuturo.get()
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                val analisis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analisis.setAnalyzer(ejecutor) { imagen -> analizarImagen(imagen, escaner, onCodigo) }

                proveedor.unbindAll()
                proveedor.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analisis)
            }, ContextCompat.getMainExecutor(contexto))
            previewView
        }
    )
}

/**
 * Busca un código QR en una imagen de la cámara con ML Kit.
 * InputImage.fromMediaImage prepara la imagen con su rotación; escaner.process la analiza.
 * Si encuentra un código, entrega su texto (rawValue) a onCodigo.
 * Al terminar siempre se cierra la imagen (close), si no la cámara deja de enviar cuadros nuevos.
 * La anotación @OptIn indica que se usa una función marcada como experimental (imageProxy.image).
 */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun analizarImagen(imagenProxy: ImageProxy, escaner: BarcodeScanner, onCodigo: (String) -> Unit) {
    val imagenCamara = imagenProxy.image
    if (imagenCamara == null) {
        imagenProxy.close()
        return
    }
    val imagen = InputImage.fromMediaImage(imagenCamara, imagenProxy.imageInfo.rotationDegrees)
    escaner.process(imagen)
        .addOnSuccessListener { codigos ->
            codigos.firstOrNull()?.rawValue?.let(onCodigo)
        }
        .addOnCompleteListener {
            imagenProxy.close()
        }
}
