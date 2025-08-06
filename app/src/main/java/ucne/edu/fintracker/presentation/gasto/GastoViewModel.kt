package ucne.edu.fintracker.presentation.gasto

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.OffsetDateTime
import ucne.edu.fintracker.data.local.repository.CategoriaRepository
import ucne.edu.fintracker.data.local.repository.TransaccionRepository
import androidx.compose.runtime.State
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.TotalAnual
import ucne.edu.fintracker.presentation.remote.dto.TotalMes
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import ucne.edu.fintracker.presentation.utils.SaldoCalculatorUtil
import javax.inject.Inject

@HiltViewModel
class GastoViewModel @Inject constructor(
    private val transaccionRepository: TransaccionRepository,
    private val categoriaRepository: CategoriaRepository,
    private val saldoCalculator: SaldoCalculatorUtil
) : ViewModel() {

    private val _totalesMensuales = mutableStateOf<List<TotalMes>>(emptyList())
    val totalesMensuales: State<List<TotalMes>> = _totalesMensuales

    private val _totalesAnuales = mutableStateOf<List<TotalAnual>>(emptyList())
    val totalesAnuales: State<List<TotalAnual>> = _totalesAnuales

    private var usuarioIdActual: Int? = null

    private val _uiState = MutableStateFlow(
        GastoUiState()
    )
    val uiState: StateFlow<GastoUiState> = _uiState

    private val _eventoEliminacion = MutableSharedFlow<Unit>()
    val eventoEliminacion = _eventoEliminacion.asSharedFlow()

    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    val transaccionesFiltradas: StateFlow<List<TransaccionDto>> =
        uiState.map { state ->
            val filtradasPorFecha = filtrarTransaccionesPorFecha(state.transacciones, state.filtro)
            filtradasPorFecha.filter {
                it.tipo.trim().equals(state.tipoSeleccionado.trim(), ignoreCase = true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun inicializar(usuarioId: Int) {
        Log.d("GastoViewModel", "Inicializando con usuarioId: $usuarioId")
        if (usuarioId <= 0) { Log.e("GastoViewModel", "usuarioId inválido")
        return}

        if (usuarioIdActual != usuarioId) {
            usuarioIdActual = usuarioId
            Log.d("GastoViewModel", "Cargando datos del usuario...")
            fetchCategorias(usuarioId)
            cargarTransacciones(usuarioId)
            cargarDatos(usuarioId)

            cargarDatos(usuarioId)
        }
    }

    fun cargarDatos(usuarioId: Int) {
            viewModelScope.launch {
                try {
                    val totalesMes = transaccionRepository.obtenerTotalesPorMes(usuarioId)
                    val totalesAno = transaccionRepository.obtenerTotalesPorAno(usuarioId)

                    _totalesMensuales.value = totalesMes
                    _totalesAnuales.value = totalesAno
                } catch (e: Exception) {
                    Log.e("GastoViewModel", "Error al cargar datos: ${e.message}", e)
                }
            }
    }


//    private val _uiState = MutableStateFlow(
//        GastoUiState(
//            transacciones = emptyList(),
//            filtro = "Dia",
//            tipoSeleccionado = "Gasto",
//            isLoading = false,
//            error = null
//        )
//    )
//    val uiState: StateFlow<GastoUiState> = _uiState
//    private val _eventoEliminacion = MutableSharedFlow<Unit>()
//    val eventoEliminacion = _eventoEliminacion.asSharedFlow()
//
//    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
//    val categorias: StateFlow<List<CategoriaDto>> = _categorias
//
//    val transaccionesFiltradas: StateFlow<List<TransaccionDto>> =
//        uiState.map { state ->
//            val filtradasPorFecha = filtrarTransaccionesPorFecha(state.transacciones, state.filtro)
//
//            val resultado = filtradasPorFecha.filter {
//                val coincide =
//                    it.tipo.trim().equals(state.tipoSeleccionado.trim(), ignoreCase = true)
//                Log.d(
//                    "FiltroTipo",
//                    "Transacción tipo=${it.tipo}, tipoSeleccionado=${state.tipoSeleccionado}, coincide=$coincide"
//                )
//                coincide
//            }
//
//            Log.d(
//                "TransaccionesFiltradas",
//                "Filtradas (${state.filtro} / ${state.tipoSeleccionado}): ${resultado.size}"
//            )
//            resultado
//        }.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.Lazily,
//            initialValue = emptyList()
//        )
//

//    private var usuarioIdActual: Int? = null
//    fun inicializar(usuarioId: Int) {
//        if (usuarioId <= 0) {
//            Log.e("GastoViewModel", "UsuarioId inválido en inicializar: $usuarioId")
//            return
//        }
//
//        if (usuarioIdActual != usuarioId) {
//            usuarioIdActual = usuarioId
//            Log.d("GastoViewModel", "Inicializando para usuarioId: $usuarioId")
//            fetchCategorias(usuarioId)
//            cargarTransacciones(usuarioId)
//        } else {
//            Log.d("GastoViewModel", "Ya inicializado para usuarioId: $usuarioIdActual")
//        }
//    }


    fun fetchCategorias(usuarioId: Int? = usuarioIdActual) {
        usuarioId?.let {
            viewModelScope.launch {
                Log.d("GastoViewModel", "Cargando categorías para usuario $it")
                categoriaRepository.getCategorias(it).collect { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            Log.d("GastoViewModel", "Categorías cargadas: ${result.data?.size}")
                            _categorias.value = result.data ?: emptyList()
                        }

                        is Resource.Error -> {
                            Log.e("GastoViewModel", "Error cargando categorías: ${result.message}")
                            _categorias.value = emptyList()
                        }
                    }
                }
            }
        } ?: Log.e("GastoViewModel", "usuarioId es null en fetchCategorias")
    }

    fun cargarTransacciones(usuarioId: Int? = usuarioIdActual) {
        usuarioId?.let {
            viewModelScope.launch {
                transaccionRepository.getTransacciones(it).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }

                        is Resource.Success -> {
                            val transacciones = result.data ?: emptyList()
                            Log.d("GastoViewModel", "Transacciones cargadas: ${transacciones.size}")
                            transacciones.forEach { transaccion ->
                                Log.d(
                                    "GastoViewModel",
                                    "ID=${transaccion.transaccionId} Tipo=${transaccion.tipo} Monto=${transaccion.monto}"
                                )
                            }

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    transacciones = transacciones,
                                    error = null
                                )
                            }
                        }

                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message ?: "Error desconocido"
                                )
                            }
                        }
                    }
                }
            }
        } ?: Log.e("GastoViewModel", "usuarioId es null en cargarTransacciones")
    }

    fun cambiarFiltro(filtro: String) {
        _uiState.update { it.copy(filtro = filtro) }
    }

    fun cambiarTipo(tipo: String) {
        Log.d("GastoViewModel", "Tipo seleccionado cambiado a: $tipo")
        _uiState.update { it.copy(tipoSeleccionado = tipo) }
        usuarioIdActual?.let { cargarDatos(it) }
    }


    fun crearTransaccion(transaccionDto: TransaccionDto) {
        Log.d("GastoViewModel", "Intentando crear transacción: $transaccionDto")
        viewModelScope.launch {
            transaccionRepository.createTransaccion(transaccionDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        val listaActual = _uiState.value.transacciones.toMutableList()
                        result.data?.let { listaActual.add(it) }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transacciones = listaActual
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
        }
    }

    fun filtrarTransaccionesPorFecha(
        transacciones: List<TransaccionDto>, filtro: String
    ): List<TransaccionDto> {
        val hoy = OffsetDateTime.now()

        return when (filtro) {
            "Día" -> {
                val hoyLocalDate = hoy.toLocalDate()
                transacciones.filter {
                    it.fecha.toLocalDate().isEqual(hoyLocalDate)
                }
            }

            "Semana" -> {
                val lunes = hoy.toLocalDate().with(DayOfWeek.MONDAY)
                val domingo = hoy.toLocalDate().with(DayOfWeek.SUNDAY)
                transacciones.filter {
                    val fecha = it.fecha.toLocalDate()
                    !fecha.isBefore(lunes) && !fecha.isAfter(domingo)
                }
            }

            "Mes" -> {
                transacciones.filter {
                    val fecha = it.fecha.toLocalDate()
                    fecha.month == hoy.toLocalDate().month && fecha.year == hoy.toLocalDate().year
                }
            }

            "Año" -> {
                transacciones.filter {
                    it.fecha.toLocalDate().year == hoy.toLocalDate().year
                }
            }

            else -> transacciones
        }
    }

    fun obtenerTransaccionPorId(id: Int): TransaccionDto? {
        return _uiState.value.transacciones.find { it.transaccionId == id }
    }


    fun actualizarTransaccion(transaccionDto: TransaccionDto) {
        viewModelScope.launch {
            transaccionRepository.updateTransaccion(transaccionDto.transaccionId, transaccionDto)
                .collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        val listaActual = _uiState.value.transacciones.toMutableList()
                        val index = listaActual.indexOfFirst { it.transaccionId == transaccionDto.transaccionId }
                        if (index != -1) {
                            listaActual[index] = transaccionDto
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transacciones = listaActual,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Error actualizando transacción"
                            )
                        }
                    }
                }
            }
        }
    }


    fun eliminarTransaccion(id: Int) {
        viewModelScope.launch {
            transaccionRepository.deleteTransaccion(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        val listaFiltrada =
                            _uiState.value.transacciones.filter { it.transaccionId != id }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transacciones = listaFiltrada,
                                error = null
                            )
                        }
                        _eventoEliminacion.emit(Unit)
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
        }
    }



}