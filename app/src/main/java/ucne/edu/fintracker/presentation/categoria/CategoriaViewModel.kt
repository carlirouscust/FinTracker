package ucne.edu.fintracker.presentation.categoria

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.data.local.repository.CategoriaRepository
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.Resource
import javax.inject.Inject

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val repository: CategoriaRepository
) : ViewModel() {

    private val _usuarioId = MutableStateFlow<Int?>(null)

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _usuarioId
                .filter { it != null && it != 0 }
                .collectLatest { id ->
                    fetchCategorias(id!!)
                }
        }
    }

    val usuarioId: StateFlow<Int?> = _usuarioId

    fun setUsuarioId(id: Int) {
        Log.d("CategoriaViewModel", "setUsuarioId: $id")
        if (id != 0) {
            _usuarioId.value = id
        }
    }

    fun getCategoriasFiltradas(): List<CategoriaDto> {
        val state = _uiState.value
        return if (state.filtroTipo.isBlank()) {
            state.categorias
        } else {
            state.categorias.filter { it.tipo == state.filtroTipo }
        }
    }

    fun fetchCategorias(usuarioId: Int) {
        if (usuarioId == 0) {
            _uiState.update { it.copy(error = "Usuario inválido", isLoading = false) }
            return
        }
        viewModelScope.launch {
            repository.getCategorias(usuarioId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { categorias ->
                            _uiState.update {
                                it.copy(
                                    categorias = categorias,
                                    isLoading = false,
                                    error = null,
                                    filtroTipo = ""
                                )
                            }
                        } ?: run {
                            _uiState.update { it.copy(isLoading = false, filtroTipo = "") }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message, isLoading = false) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun saveCategoria(usuarioId: Int, onSuccess: () -> Unit) {
        if (usuarioId == 0) {
            _uiState.update { it.copy(error = "Usuario inválido, no se puede guardar") }
            return
        }

        val current = _uiState.value
        val nuevaCategoria = CategoriaDto(
            nombre = current.nombre,
            tipo = current.tipo,
            icono = current.icono,
            colorFondo = current.colorFondo,
            usuarioId = usuarioId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.createCategoria(nuevaCategoria).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { categoriaCreada ->
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    categorias = state.categorias + categoriaCreada,
                                    nombre = "",
                                    tipo = "Gasto",
                                    icono = "",
                                    colorFondo = "FFFFFF",
                                    error = null
                                )
                            }
                        }
                        fetchCategorias(usuarioId)
                        onSuccess()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message, isLoading = false) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun categoriasFiltradas(): List<CategoriaDto> {
        val state = _uiState.value
        return state.categorias.filter { cat ->
            if (state.selectedTabIndex == 0) cat.tipo == "Gasto" else cat.tipo == "Ingreso"
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value) }
    }

    fun onTipoChange(value: String) {
        _uiState.update { it.copy(tipo = value) }
    }

    fun onFiltroTipoChange(filtro: String) {
        _uiState.update { it.copy(filtroTipo = filtro) }
    }

    fun inicializarSinFiltro() {
        _uiState.update { it.copy(filtroTipo = "") }
    }

    fun limpiarFiltro() {
        _uiState.update { it.copy(filtroTipo = "") }
    }

    fun onIconoChange(value: String) {
        _uiState.update { it.copy(icono = value) }
    }

    fun onColorChange(value: String) {
        _uiState.update { it.copy(colorFondo = value) }
    }
}