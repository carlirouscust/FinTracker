package ucne.edu.fintracker.presentation.panelUsuario

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.dto.CambiarContrasenaRequest
import javax.inject.Inject

@HiltViewModel
class CambiarContrasenaViewModel @Inject constructor(
    private val api: FinTrackerApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(CambiarContrasenaUiState())
    val uiState: StateFlow<CambiarContrasenaUiState> = _uiState.asStateFlow()

    fun cambiarContrasena(usuarioId: Int, contrasenaActual: String, nuevaContrasena: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = ""
                )

                val request = CambiarContrasenaRequest(
                    contraseñaActual = contrasenaActual,
                    contraseñaNueva = nuevaContrasena
                )

                val response = api.cambiarContrasena(usuarioId, request)

                Log.d("Contrasena", "request: $contrasenaActual")
                Log.d("Response", "request: $response")

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        mensaje = "Contraseña cambiada exitosamente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = when (response.code()) {
                            400 -> "Contraseña actual incorrecta"
                            404 -> "Usuario no encontrado"
                            else -> "Error al cambiar la contraseña"
                        }
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            isSuccess = false,
            isError = false,
            errorMessage = "",
            mensaje = ""
        )
    }
}