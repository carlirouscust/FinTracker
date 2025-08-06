package ucne.edu.fintracker.data.local.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto
import javax.inject.Inject


class MetaRepository @Inject constructor(
    private val dataSource: DataSource
) {

    fun getMetas(usuarioId: Int, metaId: Int): Flow<Resource<MetaAhorroDto?>> = flow {
        emit(Resource.Loading())
        try {
            val metas = dataSource.getMetaAhorrosPorUsuario(usuarioId)
            val meta = metas.find { it.metaAhorroId == metaId }
            emit(Resource.Success(meta))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener la meta: ${e.message ?: "Error desconocido"}"))
        }
    }


    // Crear una nueva meta
    fun createMeta(metaDto: MetaAhorroDto): Flow<Resource<MetaAhorroDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.createMetaAhorro(metaDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear meta: ${e.message ?: "Error desconocido"}"))
        }
    }

    fun updateMeta(id: Int, metaDto: MetaAhorroDto): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            Log.d("RepoMeta", "Intentando actualizar meta ID=$id con datos: $metaDto")
            val result = dataSource.updateMetaAhorro(id, metaDto)
            Log.d("RepoMeta", "Respuesta de updateMetaAhorro: $result")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("RepoMeta", "Error en updateMeta: ${e.message}", e)
            emit(Resource.Error("Error al actualizar meta: ${e.message ?: "Error desconocido"}"))
        }
    }


    // Eliminar una meta
    fun deleteMeta(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteMetaAhorro(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar meta: ${e.message ?: "Error desconocido"}"))
        }
    }
}