package ucne.edu.fintracker.presentation.remote

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun ImagenAdapter (context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "meta_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}