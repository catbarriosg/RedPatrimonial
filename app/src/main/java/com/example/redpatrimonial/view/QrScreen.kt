package com.example.redpatrimonial.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.viewmodel.QrViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions

@Composable
fun QrScreen(
    navController: NavController,
    qrViewModel: QrViewModel = viewModel()
) {
    val context = LocalContext.current

    val contenidoQR by qrViewModel.contenidoQR
        .collectAsStateWithLifecycle()

    val opciones = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }

    val scanner = remember(context, opciones) {
        GmsBarcodeScanning.getClient(context, opciones)
    }

    Scaffold(
        bottomBar = {
            BarraNavegacion(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Lector de código QR",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    navController.navigate(Routes.MAPA) {
                        popUpTo(Routes.MAPA) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            ) {
                Text("← Volver al mapa")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            qrViewModel.guardarQR(
                                barcode.rawValue.orEmpty()
                            )
                        }
                        .addOnCanceledListener {
                            // Escaneo cancelado
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(
                                context,
                                "Error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            ) {
                Text("Escanear QR")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Contenido del QR:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contenidoQR.ifEmpty {
                    "Todavía no se ha escaneado ningún código"
                }
            )
        }
    }
}
