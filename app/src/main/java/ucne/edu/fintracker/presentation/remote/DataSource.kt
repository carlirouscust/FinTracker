package ucne.edu.fintracker.presentation.remote

import retrofit2.Response
import ucne.edu.fintracker.presentation.remote.dto.CambiarContrasenaRequest
//import ucne.edu.fintracker.presentation.remote.dto.CambiarPasswordRequest
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
import ucne.edu.fintracker.presentation.remote.dto.TotalAnual
import ucne.edu.fintracker.presentation.remote.dto.TotalMes
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import javax.inject.Inject

class DataSource @Inject constructor(
    private val api: FinTrackerApi
) {
    // ------------------- USUARIOS -------------------
    suspend fun getUsuarios(): List<UsuarioDto> = api.getUsuarios()
    suspend fun createUsuario(usuario: UsuarioDto): UsuarioDto = api.createUsuario(usuario)
    suspend fun getUsuario(id: Int): UsuarioDto = api.getUsuario(id)
    suspend fun updateUsuario(id: Int, usuario: UsuarioDto): UsuarioDto = api.updateUsuario(id, usuario)
    suspend fun cambiarContrasena(usuarioId: Int, request: CambiarContrasenaRequest): Response<Unit> {
        return api.cambiarContrasena(usuarioId, request)
    }

    suspend fun deleteUsuario(id: Int) = api.deleteUsuario(id)
//    suspend fun enviarResetPassword(email: String): Boolean {
//        val response = api.enviarLinkResetPassword(CambiarPasswordRequest(email))
//        return response.isSuccessful
//    }

    // ------------------- CATEGORÍAS -------------------
    suspend fun getCategorias(): List<CategoriaDto> = api.getCategorias()
    suspend fun getCategoriasPorUsuario(usuarioId: Int): List<CategoriaDto> =
        api.getCategoriasPorUsuario(usuarioId)

    suspend fun createCategoria(categoriaDto: CategoriaDto): CategoriaDto = api.createCategoria(categoriaDto)
    suspend fun getCategoria(id: Int): CategoriaDto = api.getCategoria(id)
    suspend fun updateCategoria(id: Int, categoriaDto: CategoriaDto): CategoriaDto = api.updateCategoria(id, categoriaDto)
    suspend fun deleteCategoria(id: Int) = api.deleteCategoria(id)

    // ------------------- TRANSACCIONES -------------------
    suspend fun getTransacciones(): List<TransaccionDto> = api.getTransacciones()
    suspend fun getTransaccionesPorUsuario(usuarioId: Int): List<TransaccionDto> =
        api.getTransaccionesPorUsuario(usuarioId)

    suspend fun createTransaccion(transaccionDto: TransaccionDto): TransaccionDto = api.createTransaccion(transaccionDto)
    suspend fun getTransaccion(id: Int): TransaccionDto = api.getTransaccion(id)
    suspend fun updateTransaccion(id: Int, transaccionDto: TransaccionDto): TransaccionDto =
        api.updateTransaccion(id, transaccionDto)
    suspend fun deleteTransaccion(id: Int) = api.deleteTransaccion(id)
    suspend fun obtenerTotalesPorMes(usuarioId: Int): List<TotalMes> {
        return api.obtenerTotalesPorMes(usuarioId)
    }

    suspend fun obtenerTotalesPorAno(usuarioId: Int): List<TotalAnual> {
        return api.obtenerTotalesPorAno(usuarioId)
    }
    // ------------------- PAGO RECURRENTE -------------------
    suspend fun getPagoRecurrentes(): List<PagoRecurrenteDto> = api.getPagoRecurrentes()
    suspend fun getPagoRecurrentesPorUsuario(usuarioId: Int): List<PagoRecurrenteDto> =
        api.getPagoRecurrentesPorUsuario(usuarioId)

    suspend fun createPagoRecurrente(pagoRecurrenteDto: PagoRecurrenteDto): PagoRecurrenteDto =
        api.createPagoRecurrente(pagoRecurrenteDto)
    suspend fun getPagoRecurrente(id: Int): PagoRecurrenteDto = api.getPagoRecurrente(id)
    suspend fun updatePagoRecurrente(id: Int, pagoRecurrenteDto: PagoRecurrenteDto): Response<Void> =
        api.updatePagoRecurrente(id, pagoRecurrenteDto)
    suspend fun deletePagoRecurrente(id: Int) = api.deletePagoRecurrente(id)

    // ------------------- LIMITE DE GASTO -------------------
    suspend fun getLimiteGastos(): List<LimiteGastoDto> = api.getLimiteGastos()
    suspend fun getLimiteGastosPorUsuario(usuarioId: Int): List<LimiteGastoDto> =
        api.getLimiteGastosPorUsuario(usuarioId)

    suspend fun createLimiteGasto(limiteGastoDto: LimiteGastoDto): LimiteGastoDto = api.createLimiteGasto(limiteGastoDto)
    suspend fun getLimiteGasto(id: Int): LimiteGastoDto = api.getLimiteGasto(id)
    suspend fun updateLimiteGasto(id: Int, limiteGastoDto: LimiteGastoDto): LimiteGastoDto =
        api.updateLimiteGasto(id, limiteGastoDto)
    suspend fun deleteLimiteGasto(id: Int) = api.deleteLimiteGasto(id)

    // ------------------- META DE AHORRO -------------------
    suspend fun getMetaAhorros(): List<MetaAhorroDto> = api.getMetaAhorros()
    suspend fun getMetaAhorrosPorUsuario(usuarioId: Int): List<MetaAhorroDto> =
        api.getMetaAhorrosPorUsuario(usuarioId)

    suspend fun createMetaAhorro(metaAhorroDto: MetaAhorroDto): MetaAhorroDto = api.createMetaAhorro(metaAhorroDto)
    suspend fun getMetaAhorro(id: Int): MetaAhorroDto = api.getMetaAhorro(id)
    suspend fun updateMetaAhorro(id: Int, metaAhorroDto: MetaAhorroDto): Response<Unit> =
        api.updateMetaAhorro(id, metaAhorroDto)
    suspend fun deleteMetaAhorro(id: Int) = api.deleteMetaAhorro(id)
}



