package ucne.edu.fintracker.presentation.metaahorro

import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto

data class MetaUiState(
    val metas: List<MetaAhorroDto> = emptyList(),
    val metaSeleccionada: MetaAhorroDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val metaCreada: Boolean = false
)