package ucne.edu.fintracker.data.local.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.presentation.remote.dto.TotalAnual
import ucne.edu.fintracker.presentation.remote.dto.TotalMes
import javax.inject.Inject



class TransaccionRepository @Inject constructor(
    private val dataSource: DataSource,
) {

    // Obtener transacciones filtradas por usuarioId
    fun getTransacciones(usuarioId: Int): Flow<Resource<List<TransaccionDto>>> = flow {
        emit(Resource.Loading())
        try {
            val transacciones = dataSource.getTransaccionesPorUsuario(usuarioId)
            emit(Resource.Success(transacciones))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener transacciones: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Crear una nueva transacción
    fun createTransaccion(transaccionDto: TransaccionDto): Flow<Resource<TransaccionDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.createTransaccion(transaccionDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear transacción: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Actualizar una transacción existente
    fun updateTransaccion(id: Int, transaccionDto: TransaccionDto): Flow<Resource<TransaccionDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.updateTransaccion(id, transaccionDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar transacción: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Eliminar una transacción
    fun deleteTransaccion(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteTransaccion(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar transacción: ${e.message ?: "Error desconocido"}"))
        }
    }

    suspend fun obtenerTotalesPorMes(usuarioId: Int): List<TotalMes> {
        Log.d("TransaccionRepository", "Obteniendo totales por mes para usuarioId=$usuarioId")
        return try {
            dataSource.obtenerTotalesPorMes(usuarioId)
        } catch (e: Exception) {
            Log.e("TransaccionRepository", "Error al obtener totales por mes", e)
            emptyList()
        }
    }

    suspend fun obtenerTotalesPorAno(usuarioId: Int): List<TotalAnual> {
        Log.d("TransaccionRepository", "Obteniendo totales por año para usuarioId=$usuarioId")
        return try {
            dataSource.obtenerTotalesPorAno(usuarioId)
        } catch (e: Exception) {
            Log.e("TransaccionRepository", "Error al obtener totales por año", e)
            emptyList()
        }
    }

}
