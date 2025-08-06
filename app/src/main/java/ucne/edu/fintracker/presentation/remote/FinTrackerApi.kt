package ucne.edu.fintracker.presentation.remote

import retrofit2.Response
import retrofit2.http.*
import ucne.edu.fintracker.presentation.remote.dto.*

interface FinTrackerApi {

        // -------- Usuarios --------
        @GET("api/Usuarios")
        suspend fun getUsuarios(): List<UsuarioDto>

        @POST("api/Usuarios")
        suspend fun createUsuario(@Body usuarioDto: UsuarioDto): UsuarioDto

        @GET("api/Usuarios/{id}")
        suspend fun getUsuario(@Path("id") id: Int): UsuarioDto

        @PUT("api/Usuarios/{id}")
        suspend fun updateUsuario(@Path("id") id: Int, @Body usuarioDto: UsuarioDto): UsuarioDto

        @DELETE("api/Usuarios/{id}")
        suspend fun deleteUsuario(@Path("id") id: Int)

        @PUT("api/Usuarios/{id}/cambiarContrasena")
        suspend fun cambiarContrasena(
                @Path("id") usuarioId: Int,
                @Body request: CambiarContrasenaRequest
        ): Response<Unit>

        // -------- Transacciones --------
        @GET("api/Transacciones")
        suspend fun getTransacciones(): List<TransaccionDto>

        @GET("api/Transacciones/PorUsuario/{usuarioId}")
        suspend fun getTransaccionesPorUsuario(@Path("usuarioId") usuarioId: Int): List<TransaccionDto>

        @POST("api/Transacciones")
        suspend fun createTransaccion(@Body transaccionDto: TransaccionDto): TransaccionDto

        @GET("api/Transacciones/{id}")
        suspend fun getTransaccion(@Path("id") id: Int): TransaccionDto

        @PUT("api/Transacciones/{id}")
        suspend fun updateTransaccion(@Path("id") id: Int, @Body transaccionDto: TransaccionDto): TransaccionDto

        @DELETE("api/Transacciones/{id}")
        suspend fun deleteTransaccion(@Path("id") id: Int)

        @GET("api/Transacciones/totales-mensuales/{usuarioId}")
        suspend fun obtenerTotalesPorMes(
                @Path("usuarioId") usuarioId: Int
        ): List<TotalMes>

        @GET("api/Transacciones/totales-anuales/{usuarioId}")
        suspend fun obtenerTotalesPorAno(
                @Path("usuarioId") usuarioId: Int
        ): List<TotalAnual>

        // -------- Pago recurrente --------
        @GET("api/PagoRecurrentes")
        suspend fun getPagoRecurrentes(): List<PagoRecurrenteDto>

        @GET("api/PagoRecurrentes/PorUsuario/{usuarioId}")
        suspend fun getPagoRecurrentesPorUsuario(@Path("usuarioId") usuarioId: Int): List<PagoRecurrenteDto>

        @POST("api/PagoRecurrentes")
        suspend fun createPagoRecurrente(@Body pagoRecurrenteDto: PagoRecurrenteDto): PagoRecurrenteDto

        @GET("api/PagoRecurrentes/{id}")
        suspend fun getPagoRecurrente(@Path("id") id: Int): PagoRecurrenteDto

        @PUT("api/PagoRecurrentes/{id}")
        suspend fun updatePagoRecurrente(
                @Path("id") id: Int,
                @Body pagoRecurrenteDto: PagoRecurrenteDto
        ): Response<Void>

        @DELETE("api/PagoRecurrentes/{id}")
        suspend fun deletePagoRecurrente(@Path("id") id: Int)

        // -------- Metas de ahorro --------
        @GET("api/MetaAhorros")
        suspend fun getMetaAhorros(): List<MetaAhorroDto>

        @GET("api/MetaAhorros/PorUsuario/{usuarioId}")
        suspend fun getMetaAhorrosPorUsuario(@Path("usuarioId") usuarioId: Int): List<MetaAhorroDto>

        @POST("api/MetaAhorros")
        suspend fun createMetaAhorro(@Body metaAhorroDto: MetaAhorroDto): MetaAhorroDto

        @GET("api/MetaAhorros/{id}")
        suspend fun getMetaAhorro(@Path("id") id: Int): MetaAhorroDto

        @PUT("api/MetaAhorros/{id}")
        suspend fun updateMetaAhorro(@Path("id") id: Int, @Body metaAhorroDto: MetaAhorroDto): Response<Unit>

        @DELETE("api/MetaAhorros/{id}")
        suspend fun deleteMetaAhorro(@Path("id") id: Int)

        // -------- Límites de gasto --------
        @GET("api/LimiteGastos")
        suspend fun getLimiteGastos(): List<LimiteGastoDto>

        @GET("api/LimiteGastos/PorUsuario/{usuarioId}")
        suspend fun getLimiteGastosPorUsuario(@Path("usuarioId") usuarioId: Int): List<LimiteGastoDto>

        @POST("api/LimiteGastos")
        suspend fun createLimiteGasto(@Body limiteGastoDto: LimiteGastoDto): LimiteGastoDto

        @GET("api/LimiteGastos/{id}")
        suspend fun getLimiteGasto(@Path("id") id: Int): LimiteGastoDto

        @PUT("api/LimiteGastos/{id}")
        suspend fun updateLimiteGasto(@Path("id") id: Int, @Body limiteGastoDto: LimiteGastoDto): LimiteGastoDto

        @DELETE("api/LimiteGastos/{id}")
        suspend fun deleteLimiteGasto(@Path("id") id: Int)

        // -------- Categorías --------
        @GET("api/Categorias")
        suspend fun getCategorias(): List<CategoriaDto>

        @GET("api/Categorias/PorUsuario/{usuarioId}")
        suspend fun getCategoriasPorUsuario(@Path("usuarioId") usuarioId: Int): List<CategoriaDto>

        @POST("api/Categorias")
        suspend fun createCategoria(@Body categoriaDto: CategoriaDto): CategoriaDto

        @GET("api/Categorias/{id}")
        suspend fun getCategoria(@Path("id") id: Int): CategoriaDto

        @PUT("api/Categorias/{id}")
        suspend fun updateCategoria(@Path("id") id: Int, @Body categoriaDto: CategoriaDto): CategoriaDto

        @DELETE("api/Categorias/{id}")
        suspend fun deleteCategoria(@Path("id") id: Int)
}
