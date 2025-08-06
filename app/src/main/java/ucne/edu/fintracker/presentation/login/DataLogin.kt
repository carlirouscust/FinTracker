package ucne.edu.fintracker.presentation.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object DataLogin {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "usuario_prefs")

    private val USUARIO_ID = intPreferencesKey("usuario_id")
    private val SESION_ACTIVA = booleanPreferencesKey("sesion_activa")

    suspend fun guardarUsuarioId(context: Context, id: Int) {
        context.dataStore.edit { prefs ->
            prefs[USUARIO_ID] = id
            prefs[SESION_ACTIVA] = true // Marcar sesión como activa
        }
    }

    suspend fun obtenerUsuarioId(context: Context): Int? {
        val prefs = context.dataStore.data.first()
        val sesionActiva = prefs[SESION_ACTIVA] ?: false
        return if (sesionActiva) {
            prefs[USUARIO_ID]
        } else {
            null // Si la sesión no está activa, retornar null
        }
    }

    suspend fun limpiarSesion(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[SESION_ACTIVA] = false // Marcar sesión como inactiva
            // Opcionalmente puedes mantener el usuarioId para recordar el último usuario
            // o limpiarlo completamente con: prefs.clear()
        }
    }

    suspend fun verificarSesionActiva(context: Context): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[SESION_ACTIVA] ?: false
    }
}