package ucne.edu.fintracker.presentation.panelUsuario

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import javax.inject.Inject

@HiltViewModel
class CambiarFotoViewModel @Inject constructor(
    private val api: FinTrackerApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(CambiarFotoUiState())
    val uiState: StateFlow<CambiarFotoUiState> = _uiState.asStateFlow()

    fun cargarDatosUsuario(usuarioId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = ""
                )

                val usuario = api.getUsuario(usuarioId)

                Log.d("CambiarFoto", "Usuario cargado: $usuario")

                _uiState.value = _uiState.value.copy(
                    usuarioId = usuarioId,
                    nombreUsuario = usuario.nombre,
                    apellidoUsuario = usuario.apellido,
                    fotoPerfilUrl = usuario.fotoPerfil,
                    isLoading = false,
                    isError = false,
                    errorMessage = ""
                )

            } catch (e: HttpException) {
                Log.e("CambiarFoto", "HttpException: ${e.code()} - ${e.message()}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = when (e.code()) {
                        404 -> "Usuario no encontrado"
                        401 -> "No autorizado"
                        500 -> "Error del servidor"
                        else -> "Error al cargar los datos del usuario"
                    }
                )
            } catch (e: Exception) {
                Log.e("CambiarFoto", "Error al cargar usuario: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun seleccionarFotoDesdeGaleria(uri: Uri?) {
        Log.d("CambiarFoto", "Foto seleccionada desde galería: $uri")
        _uiState.value = _uiState.value.copy(
            fotoPerfilUri = uri,
            mostrarOpcionesFoto = false,
            fotoGuardadaExitosamente = false
        )
    }

    fun seleccionarFotoDesdeCamara(uri: Uri?) {
        Log.d("CambiarFoto", "Foto tomada desde cámara: $uri")
        _uiState.value = _uiState.value.copy(
            fotoPerfilUri = uri,
            mostrarOpcionesFoto = false,
            fotoGuardadaExitosamente = false
        )
    }

    fun mostrarOpcionesFoto() {
        _uiState.value = _uiState.value.copy(
            mostrarOpcionesFoto = true
        )
    }

    fun ocultarOpcionesFoto() {
        _uiState.value = _uiState.value.copy(
            mostrarOpcionesFoto = false
        )
    }

    fun guardarFotoPerfil() {
        val usuarioId = _uiState.value.usuarioId
        val fotoUri = _uiState.value.fotoPerfilUri

        if (usuarioId == null) {
            _uiState.value = _uiState.value.copy(
                isError = true,
                errorMessage = "Error: ID de usuario no válido"
            )
            return
        }

        if (fotoUri == null) {
            _uiState.value = _uiState.value.copy(
                isError = true,
                errorMessage = "Por favor selecciona una foto antes de guardar"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isUploadingFoto = true,
                    isError = false,
                    errorMessage = ""
                )

                // Aquí deberías implementar la lógica para subir la foto
                // Por ejemplo, convertir la URI a MultipartBody.Part y subirla
                // val result = api.subirFotoPerfil(usuarioId, fotoPart)

                Log.d("CambiarFoto", "Simulando subida de foto para usuario: $usuarioId")

                // Simulación de delay para subida
                kotlinx.coroutines.delay(2000)

                // Simular éxito (reemplaza esto con la llamada real a la API)
                _uiState.value = _uiState.value.copy(
                    isUploadingFoto = false,
                    fotoGuardadaExitosamente = true,
                    fotoPerfilUrl = fotoUri.toString(), // En realidad sería la URL devuelta por el servidor
                    isError = false,
                    errorMessage = ""
                )

                Log.d("CambiarFoto", "Foto guardada exitosamente")

            } catch (e: HttpException) {
                Log.e("CambiarFoto", "HttpException al guardar foto: ${e.code()} - ${e.message()}")
                _uiState.value = _uiState.value.copy(
                    isUploadingFoto = false,
                    isError = true,
                    errorMessage = when (e.code()) {
                        400 -> "Formato de imagen no válido"
                        413 -> "La imagen es demasiado grande"
                        401 -> "No autorizado"
                        500 -> "Error del servidor"
                        else -> "Error al guardar la foto"
                    }
                )
            } catch (e: Exception) {
                Log.e("CambiarFoto", "Error al guardar foto: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isUploadingFoto = false,
                    isError = true,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(
            isError = false,
            errorMessage = ""
        )
    }

    fun limpiarEstadoExito() {
        _uiState.value = _uiState.value.copy(
            fotoGuardadaExitosamente = false
        )
    }
}