package ucne.edu.fintracker.presentation.panelUsuario

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
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import ucne.edu.fintracker.presentation.utils.SaldoCalculatorUtil
import javax.inject.Inject

@HiltViewModel
class PanelUsuarioViewModel @Inject constructor(
    private val api: FinTrackerApi,
    private val saldoCalculatorUtil: SaldoCalculatorUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(PanelUsuarioUiState())
    val uiState: StateFlow<PanelUsuarioUiState> = _uiState.asStateFlow()

    fun cargarUsuario(usuarioId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = ""
                )

                val usuario = api.getUsuario(usuarioId)

                Log.d("PanelUsuario", "Usuario cargado: $usuario")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    usuario = usuario,
                    isError = false,
                    errorMessage = ""
                )

                // Automáticamente actualizar el saldo después de cargar el usuario
                actualizarSaldoUsuarioAutomatico(usuarioId)

            } catch (e: HttpException) {
                Log.e("PanelUsuario", "HttpException: ${e.code()} - ${e.message()}")
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
                Log.e("PanelUsuario", "Error al cargar usuario: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    private fun actualizarSaldoUsuarioAutomatico(usuarioId: Int) {
        viewModelScope.launch {
            try {
                Log.d("PanelUsuario", "Actualizando saldo automáticamente para usuario: $usuarioId")

                // Mostrar indicador de carga específico para actualización de saldo
                _uiState.value = _uiState.value.copy(
                    isUpdatingSaldo = true
                )

                // Usar el SaldoCalculatorUtil para actualizar el saldo
                val usuarioActualizado = saldoCalculatorUtil.actualizarSaldoUsuario(usuarioId)

                if (usuarioActualizado != null) {
                    Log.d("PanelUsuario", "Saldo actualizado exitosamente: ${usuarioActualizado.saldoTotal}")

                    _uiState.value = _uiState.value.copy(
                        usuario = usuarioActualizado,
                        isUpdatingSaldo = false,
                        isError = false,
                        errorMessage = ""
                    )
                } else {
                    Log.d("PanelUsuario", "SaldoCalculatorUtil devolvió null, manteniendo usuario actual")
                    _uiState.value = _uiState.value.copy(
                        isUpdatingSaldo = false
                    )
                }

            } catch (e: Exception) {
                Log.e("PanelUsuario", "Error al actualizar saldo automáticamente: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isUpdatingSaldo = false
                )
                // No mostrar error en la UI para actualizaciones automáticas silenciosas
            }
        }
    }

    // Método público para forzar actualización manual si es necesario
    fun actualizarSaldoUsuario(usuarioId: Int) {
        viewModelScope.launch {
            try {
                Log.d("PanelUsuario", "Actualizando saldo manualmente para usuario: $usuarioId")

                // Mostrar indicador de carga específico para actualización de saldo
                _uiState.value = _uiState.value.copy(
                    isUpdatingSaldo = true,
                    isError = false,
                    errorMessage = ""
                )

                // Usar el SaldoCalculatorUtil para actualizar el saldo
                val usuarioActualizado = saldoCalculatorUtil.actualizarSaldoUsuario(usuarioId)

                if (usuarioActualizado != null) {
                    Log.d("PanelUsuario", "Saldo actualizado exitosamente: ${usuarioActualizado.saldoTotal}")

                    _uiState.value = _uiState.value.copy(
                        usuario = usuarioActualizado,
                        isUpdatingSaldo = false,
                        isError = false,
                        errorMessage = ""
                    )
                } else {
                    Log.e("PanelUsuario", "Error: No se pudo actualizar el saldo")
                    _uiState.value = _uiState.value.copy(
                        isUpdatingSaldo = false,
                        isError = true,
                        errorMessage = "No se pudo actualizar el saldo. Intenta de nuevo."
                    )
                }

            } catch (e: Exception) {
                Log.e("PanelUsuario", "Error al actualizar saldo: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isUpdatingSaldo = false,
                    isError = true,
                    errorMessage = "Error al actualizar saldo: ${e.message}"
                )
            }
        }
    }

    // Método para actualizar saldo cuando se detecten cambios en transacciones
    fun notificarCambioEnTransacciones(usuarioId: Int) {
        Log.d("PanelUsuario", "Cambio detectado en transacciones, actualizando saldo automáticamente")
        actualizarSaldoUsuarioAutomatico(usuarioId)
    }

    fun actualizarUsuario(usuarioId: Int, usuario: UsuarioDto) {
        viewModelScope.launch {
            try {
                val usuarioActualizado = api.updateUsuario(usuarioId, usuario)

                Log.d("PanelUsuario", "Usuario actualizado: $usuarioActualizado")

                _uiState.value = _uiState.value.copy(
                    usuario = usuarioActualizado,
                    isError = false,
                    errorMessage = ""
                )

                // Automáticamente actualizar el saldo después de actualizar el usuario
                actualizarSaldoUsuarioAutomatico(usuarioId)

            } catch (e: HttpException) {
                Log.e("PanelUsuario", "HttpException al actualizar: ${e.code()} - ${e.message()}")
                _uiState.value = _uiState.value.copy(
                    isError = true,
                    errorMessage = when (e.code()) {
                        404 -> "Usuario no encontrado"
                        400 -> "Datos inválidos"
                        401 -> "No autorizado"
                        500 -> "Error del servidor"
                        else -> "Error al actualizar usuario"
                    }
                )
            } catch (e: Exception) {
                Log.e("PanelUsuario", "Error al actualizar usuario: ${e.message}")
                _uiState.value = _uiState.value.copy(
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
}