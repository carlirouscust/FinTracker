package ucne.edu.fintracker.presentation.utils

import android.util.Log
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaldoCalculatorUtil @Inject constructor(
    private val api: FinTrackerApi
) {

    suspend fun actualizarSaldoUsuario(usuarioId: Int): UsuarioDto? {
        return try {
            Log.d("SaldoCalculator", "Iniciando cálculo de saldo para usuario: $usuarioId")

            // 1. Obtener todas las transacciones del usuario
            val transacciones = api.getTransaccionesPorUsuario(usuarioId)
            Log.d("SaldoCalculator", "Transacciones obtenidas: ${transacciones.size}")

            // 2. Calcular saldo total: ingresos - gastos
            val saldoCalculado = transacciones.sumOf { transaccion ->
                when (transaccion.tipo?.lowercase()) {
                    "ingreso" -> {
                        Log.d("SaldoCalculator", "Ingreso: ${transaccion.monto}")
                        transaccion.monto
                    }
                    "gasto" -> {
                        Log.d("SaldoCalculator", "Gasto: -${transaccion.monto}")
                        -transaccion.monto
                    }
                    else -> {
                        Log.w("SaldoCalculator", "Tipo de transacción desconocido: ${transaccion.tipo}")
                        0.0
                    }
                }
            }

            Log.d("SaldoCalculator", "Saldo total calculado: $saldoCalculado")

            // 3. Obtener usuario actual
            val usuarioActual = api.getUsuario(usuarioId)
            Log.d("SaldoCalculator", "Usuario actual: $usuarioActual")

            // 4. Actualizar usuario con el nuevo saldo
            val usuarioActualizado = usuarioActual.copy(saldoTotal = saldoCalculado)

            // 5. Guardar en la API
            val usuarioGuardado = api.updateUsuario(usuarioId, usuarioActualizado)
            Log.d("SaldoCalculator", "Usuario guardado con nuevo saldo: $usuarioGuardado")

            usuarioGuardado

        } catch (e: Exception) {
            Log.e("SaldoCalculator", "Error al actualizar saldo del usuario: ${e.message}", e)
            null
        }
    }

    suspend fun calcularSaldoLocal(usuarioId: Int): Double {
        return try {
            val transacciones = api.getTransaccionesPorUsuario(usuarioId)
            transacciones.sumOf { transaccion ->
                when (transaccion.tipo?.lowercase()) {
                    "ingreso" -> transaccion.monto
                    "gasto" -> -transaccion.monto
                    else -> 0.0
                }
            }
        } catch (e: Exception) {
            Log.e("SaldoCalculator", "Error al calcular saldo local: ${e.message}")
            0.0
        }
    }
}