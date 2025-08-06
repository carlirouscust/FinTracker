package ucne.edu.fintracker.presentation.panelUsuario

import android.net.Uri

data class CambiarFotoUiState(
    val usuarioId: Int? = null,
    val nombreUsuario: String = "",
    val apellidoUsuario: String = "",
    val fotoPerfilUri: Uri? = null,
    val fotoPerfilUrl: String? = null,
    val isLoading: Boolean = false,
    val isUploadingFoto: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val fotoGuardadaExitosamente: Boolean = false,
    val mostrarOpcionesFoto: Boolean = false
)