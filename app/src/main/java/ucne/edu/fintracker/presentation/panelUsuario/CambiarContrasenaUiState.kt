package ucne.edu.fintracker.presentation.panelUsuario

data class CambiarContrasenaUiState(
    val isLoading: Boolean = false,
    val mensaje: String = "",
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)