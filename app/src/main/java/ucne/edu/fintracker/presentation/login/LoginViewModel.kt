package ucne.edu.fintracker.presentation.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import ucne.edu.fintracker.data.local.repository.LoginRepository
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    private val _usuarioLogueado = MutableStateFlow<UsuarioDto?>(null)
    val usuarioLogueado: StateFlow<UsuarioDto?> = _usuarioLogueado

    fun login(context: Context) {
        viewModelScope.launch {
            try {
                val usuario = loginRepository.login(
                    _uiState.value.loginEmail,
                    _uiState.value.loginPassword,
                    context
                )
                if (usuario != null) {
                    Log.d("LoginViewModel", "Usuario logueado: ${usuario.usuarioId}")
                    _usuarioLogueado.value = usuario
                    _uiState.update {
                        it.copy(
                            usuarioId = usuario.usuarioId ?: 0,
                            loginError = false
                        )
                    }
                } else {
                    Log.d("LoginViewModel", "Login fallido")
                    _usuarioLogueado.value = null
                    _uiState.update { it.copy(loginError = true) }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error login: ${e.message}")
                _usuarioLogueado.value = null
                _uiState.update { it.copy(loginError = true) }
            }
        }
    }

    fun registerUser(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                val state = uiState.value
                val newUser = UsuarioDto(
                    usuarioId = 0,
                    nombre = state.registerNombre,
                    apellido = state.registerApellido,
                    email = state.registerEmail,
                    contraseña = state.registerPassword,
                    divisa = "DOP"
                )
                loginRepository.register(newUser)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
//            try {
//                val success = loginRepository.enviarResetPassword(uiState.value.resetEmail)
//                if (success) {
//                    println("Enlace enviado")
//                } else {
//                    println("Error al enviar enlace")
//                }
//            } catch (e: Exception) {
//                println("Excepción: ${e.message}")
//            }
        }
    }

    fun onLoginEmailChange(value: String) {
        _uiState.update { it.copy(loginEmail = value) }
    }

    fun onLoginPasswordChange(value: String) {
        _uiState.update { it.copy(loginPassword = value) }
    }

    fun onRegisterNombreChange(value: String) {
        _uiState.update { it.copy(registerNombre = value) }
    }


    fun onRegisterApellidoChange(value: String) {
        _uiState.update { it.copy(registerApellido = value) }
    }


    fun onRegisterEmailChange(value: String) {
        _uiState.update { it.copy(registerEmail = value) }
    }

    fun onRegisterPasswordChange(value: String) {
        _uiState.update { it.copy(registerPassword = value) }
    }


    fun onResetEmailChange(value: String) {
        _uiState.update { it.copy(resetEmail = value, resetError = null, resetSuccess = null) }
    }


    fun changeTab(index: Int) {
        _uiState.update { it.copy(tabIndex = index) }
    }
}

