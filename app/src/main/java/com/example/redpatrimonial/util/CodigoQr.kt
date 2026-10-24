package com.example.redpatrimonial.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Formato de los códigos QR de la red: redpatrimonial://recinto/{id}.
 * Lo usan el lector QR (para leer) y el panel del encargado (para generar el código de su recinto).
 */
object CodigoQr {

    private const val PREFIJO = "redpatrimonial://recinto/"

    /**
     * Texto que va dentro del QR de un recinto, por ejemplo "redpatrimonial://recinto/3".
     */
    fun contenidoRecinto(idRecinto: Int): String = "$PREFIJO$idRecinto"

    /**
     * Devuelve el id del recinto que viene en el texto del QR, o null si no es un código de la red.
     * También acepta solo el número ("3") para poder probar escribiendo el código a mano en el emulador.
     * toIntOrNull() convierte el texto a número sin lanzar error: si no es un número, devuelve null.
     */
    fun leerIdRecinto(texto: String): Int? {
        val limpio = texto.trim()
        return if (limpio.startsWith(PREFIJO)) {
            limpio.removePrefix(PREFIJO).toIntOrNull()
        } else {
            limpio.toIntOrNull()
        }
    }

    /**
     * Dibuja un código QR con la librería ZXing y lo devuelve como imagen para Compose.
     * 1. QRCodeWriter().encode crea una matriz de cuadros: true = negro, false = blanco.
     * 2. Se crea un Bitmap del mismo tamaño y se pinta pixel por pixel.
     * 3. asImageBitmap() lo convierte al tipo de imagen que usa el composable Image.
     */
    fun generar(contenido: String, tamano: Int = 512): ImageBitmap {
        val matriz = QRCodeWriter().encode(contenido, BarcodeFormat.QR_CODE, tamano, tamano)
        val bitmap = Bitmap.createBitmap(tamano, tamano, Bitmap.Config.RGB_565)
        for (x in 0 until tamano) {
            for (y in 0 until tamano) {
                bitmap.setPixel(x, y, if (matriz[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap.asImageBitmap()
    }
}
