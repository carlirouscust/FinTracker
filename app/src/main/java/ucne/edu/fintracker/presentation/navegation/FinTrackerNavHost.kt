package ucne.edu.fintracker.presentation.navegation

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ucne.edu.fintracker.presentation.categoria.CategoriaListScreen
import ucne.edu.fintracker.presentation.categoria.CategoriaScreen
import ucne.edu.fintracker.presentation.categoria.CategoriaViewModel
import ucne.edu.fintracker.presentation.gasto.GastoListScreen
import ucne.edu.fintracker.presentation.gasto.GastoScreen
import ucne.edu.fintracker.presentation.login.LoginViewModel
import ucne.edu.fintracker.presentation.login.LoginRegisterScreen
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import androidx.navigation.NavType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.navArgument
import ucne.edu.fintracker.presentation.ajustes.AjustesListScreen
import org.threeten.bp.ZoneOffset
import ucne.edu.fintracker.presentation.remote.DateUtil
import ucne.edu.fintracker.presentation.limitegasto.LimiteDetalleScreen
import ucne.edu.fintracker.presentation.asesorIA.ChatIaScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteListScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteViewModel
import ucne.edu.fintracker.presentation.metaahorro.MetaDetalleScreen
import ucne.edu.fintracker.presentation.metaahorro.MetaListScreen
import ucne.edu.fintracker.presentation.metaahorro.MetaScreen
import ucne.edu.fintracker.presentation.metaahorro.MetaViewModel
import ucne.edu.fintracker.presentation.pagorecurrente.PagoDetalleScreen
import ucne.edu.fintracker.presentation.pagorecurrente.PagoListScreen
import ucne.edu.fintracker.presentation.pagorecurrente.PagoScreen
import ucne.edu.fintracker.presentation.pagorecurrente.PagoViewModel
import ucne.edu.fintracker.presentation.gasto.GastoViewModel
import ucne.edu.fintracker.presentation.panelUsuario.PanelUsuarioScreen
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import ucne.edu.fintracker.presentation.ajustes.AparienciaScreen
import ucne.edu.fintracker.presentation.ajustes.NotificacionesScreen
import ucne.edu.fintracker.presentation.gasto.GastoDetalleScreen
import ucne.edu.fintracker.presentation.gasto.GraficoScreen
import ucne.edu.fintracker.presentation.login.DataLogin
import ucne.edu.fintracker.presentation.metaahorro.MetaMAhorroScreen
import ucne.edu.fintracker.presentation.panelUsuario.CambiarContrasenaScreen
import ucne.edu.fintracker.presentation.panelUsuario.CambiarFotoScreen
import org.threeten.bp.OffsetDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinTrackerNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    finTrackerApi: FinTrackerApi = hiltViewModel()
) {
    val context = LocalContext.current
    var pantallaInicial by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val logueado = verificarSesion(context)
        pantallaInicial = if (logueado) "gastos" else "login"
    }
    pantallaInicial?.let { destino ->
        NavHost(
            navController = navHostController,
            startDestination = destino,
            modifier = modifier
        ) {
            composable("login") {
                LoginRegisterScreen(
                    navController = navHostController,
                    viewModel = loginViewModel
                )
            }
            composable("register") {
                LoginRegisterScreen(
                    navController = navHostController,
                    viewModel = loginViewModel
                )
            }



            composable("categoria/{tipo}") { backStackEntry ->
                val tipo = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
                val categoriaVM = hiltViewModel<CategoriaViewModel>()
                val loginState = loginViewModel.uiState.collectAsState().value
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }


                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        categoriaVM.setUsuarioId(usuarioId)
                    }
                }

                CategoriaListScreen(
                    viewModel = categoriaVM,
                    tipoFiltro = tipo,
                    usuarioId = usuarioId,
                    onBackClick = { navHostController.popBackStack() },
                    onAgregarCategoriaClick = { tipoActual ->
                        navHostController.navigate("categoria_nueva/$tipoActual")
                    },
                )
            }

            composable("categoria_nueva/{tipo}") { backStackEntry ->
                val tipo = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
                val categoriaVM = hiltViewModel<CategoriaViewModel>()
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                Log.d("CategoriaNueva", "usuarioId: $usuarioId")


                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        categoriaVM.setUsuarioId(usuarioId)
                        categoriaVM.onTipoChange(tipo)
                    }
                }

                if (usuarioId == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    CategoriaScreen(
                        navController = navHostController,
                        viewModel = categoriaVM,
                        usuarioId = usuarioId,
                        tipo = tipo,
                        onGuardar = { _, _, _, _ ->
                            categoriaVM.saveCategoria(usuarioId) {
                                navHostController.popBackStack()
                            }
                        },
                        onCancel = { navHostController.popBackStack() }
                    )
                }
            }


            composable("gastos") { backStackEntry ->
                val loginViewModel = hiltViewModel<LoginViewModel>()
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                Log.d("NavHost", "usuarioId en gastos: $usuarioId")

                val gastoViewModel = hiltViewModel<GastoViewModel>()
                val categoriaViewModel = hiltViewModel<CategoriaViewModel>()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        gastoViewModel.inicializar(usuarioId)
                        categoriaViewModel.fetchCategorias(usuarioId)
                    } else {
                        Log.e("NavHost", "usuarioId inválido o no disponible en gastos")
                    }
                }

                if (usuarioId != 0) {
                    GastoListScreen(
                        viewModel = gastoViewModel,
                        usuarioId = usuarioId,
                        categoriaViewModel = categoriaViewModel,
                        onNuevoClick = {
                            val tipo = "Gasto"
                            navHostController.navigate("gasto_nuevo/$tipo/$usuarioId")
                        },
                        navController = navHostController
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando usuario...", modifier = Modifier.padding(16.dp))
                    }
                }
            }


            composable(
                "gasto_nuevo/{tipo}/{usuarioId}",
                arguments = listOf(
                    navArgument("tipo") { type = NavType.StringType },
                    navArgument("usuarioId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val tipoInicial = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
                val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0

                val gastoViewModel: GastoViewModel = hiltViewModel()
                val categoriaViewModel: CategoriaViewModel = hiltViewModel()
                val loginViewModel: LoginViewModel = hiltViewModel()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        Log.d("NavHost", "Usuario logueado con ID: $usuarioId")
                        categoriaViewModel.fetchCategorias(usuarioId)
                        gastoViewModel.inicializar(usuarioId)
                    } else {
                        Log.e("NavHost", "usuarioId inválido o no disponible")
                    }
                }


                val categoriaUiState by categoriaViewModel.uiState.collectAsState()
                val categoriasFiltradas = categoriaUiState.categorias

                if (usuarioId != 0) {
                    GastoScreen(
                        categorias = categoriasFiltradas,
                        tipoInicial = tipoInicial,
                        usuarioId = usuarioId,
                        onGuardar = { tipoSeleccionado, monto, categoriaNombre, fechaStr, notas, usuarioIdGuardado ->
                            val categoriaId =
                                categoriasFiltradas.find { it.nombre == categoriaNombre }?.categoriaId
                                    ?: 0
                            val fechaOffsetDateTime =
                                DateUtil.parseFecha(fechaStr).atOffset(ZoneOffset.UTC)
                            Log.d(
                                "PagoScreen",
                                "Guardando pago con monto=$monto, categoriaId=$categoriaId, usuarioId=$usuarioIdGuardado"
                            )
                            gastoViewModel.crearTransaccion(
                                TransaccionDto(
                                    transaccionId = 0,
                                    monto = monto,
                                    categoriaId = categoriaId,
                                    fecha = fechaOffsetDateTime,
                                    notas = notas,
                                    tipo = tipoSeleccionado,
                                    usuarioId = usuarioIdGuardado
                                )
                            )
                            navHostController.navigate("gastos") {
                                popUpTo("gastos") { inclusive = true }
                            }
                        },
                        onCancel = {
                            navHostController.popBackStack()
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando usuario...", modifier = Modifier.padding(16.dp))
                    }
                }
            }

            composable(
                route = "gasto_detalle/{usuarioId}/{trasaccionId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("trasaccionId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                val trasaccionId = backStackEntry.arguments?.getInt("trasaccionId") ?: 0
                val gastoViewModel = hiltViewModel<GastoViewModel>()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        gastoViewModel.cargarTransacciones(usuarioId)
                        gastoViewModel.fetchCategorias(usuarioId)
                    }
                }

                val uiState by gastoViewModel.uiState.collectAsState()
                val categorias by gastoViewModel.categorias.collectAsState()

                val gasto = uiState.transacciones.find { it.transaccionId == trasaccionId }

                if (gasto != null) {
                    val categoria = categorias.find { it.categoriaId == gasto.categoriaId }
                    val categoriaIcono = categoria?.icono ?: "💸"
                    val categoriaNombre = categoria?.nombre ?: "Sin categoría"

                    GastoDetalleScreen(
                        transaccionId = trasaccionId,
                        categoriaIcono = categoriaIcono,
                        categoriaNombre = categoriaNombre,
                        onBackClick = { navHostController.popBackStack() },
                        onEditarClick = {
                            navHostController.navigate("gasto_editar/$usuarioId/$trasaccionId")
                        },
                        onEliminarClick = { /* Mostrar diálogo */ },
                        onEliminarConfirmado = {
                            gastoViewModel.eliminarTransaccion(trasaccionId)
                            navHostController.popBackStack()
                        }
                    )
                }
            }

            composable(
                route = "gasto_editar/{usuarioId}/{gastoId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("gastoId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                val gastoId = backStackEntry.arguments?.getInt("gastoId") ?: 0
                val gastoViewModel = hiltViewModel<GastoViewModel>()
                val categoriaViewModel = hiltViewModel<CategoriaViewModel>()
                val uiState by gastoViewModel.uiState.collectAsState()
                val categoriaUiState by categoriaViewModel.uiState.collectAsState()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        gastoViewModel.inicializar(usuarioId)
                        categoriaViewModel.fetchCategorias(usuarioId)
                    }
                }

                val transaccion = uiState.transacciones.find { it.transaccionId == gastoId }
                val categoriasFiltradas = categoriaUiState.categorias

                if (transaccion != null) {
                    GastoScreen(
                        categorias = categoriasFiltradas,
                        tipoInicial = transaccion.tipo,
                        transaccionParaEditar = transaccion,
                        usuarioId = usuarioId,
                        onGuardar = { tipoSeleccionado, monto, categoriaNombre, fechaStr, notas, usuarioIdGuardado ->
                            val categoriaId = categoriasFiltradas.find { it.nombre == categoriaNombre }?.categoriaId ?: 0
                            val fechaOffsetDateTime = DateUtil.parseFecha(fechaStr).atOffset(ZoneOffset.UTC)

                            gastoViewModel.actualizarTransaccion(
                                TransaccionDto(
                                    transaccionId = transaccion.transaccionId,
                                    monto = monto,
                                    categoriaId = categoriaId,
                                    fecha = fechaOffsetDateTime,
                                    notas = notas,
                                    tipo = tipoSeleccionado,
                                    usuarioId = usuarioIdGuardado
                                )
                            )

                            navHostController.navigate("gastos") {
                                popUpTo("gastos") { inclusive = true }
                            }
                        },
                        onCancel = {
                            navHostController.popBackStack()
                        }
                    )
                }
            }



            composable("pagos/{usuarioId}") {
                val pagoViewModel = hiltViewModel<PagoViewModel>()
                val categorias by pagoViewModel.categorias.collectAsState()
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                Log.d("Pagos", "usuarioId en pagos: $usuarioId")

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        pagoViewModel.cargarPagosRecurrentes(usuarioId)
                        pagoViewModel.fetchCategorias(usuarioId)
                    }
                }

                PagoListScreen(
                    viewModel = pagoViewModel,
                    categorias = categorias,
                    onAgregarPagoClick = {
                        Log.d("Pagos", "Navegando a pago_nuevo con usuarioId: $usuarioId")
                        navHostController.navigate("pago_nuevo/$usuarioId")
                    },
                    onBackClick = {
                        navHostController.popBackStack()
                    },
                    onPagoClick = { pagoId ->
                        navHostController.navigate("pago_detalle/$usuarioId/$pagoId")
                    }
                )
            }

            composable(
                "pago_nuevo/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) { backStackEntry ->

                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                Log.d("PagoNuevo", "usuarioId recibido en ruta: $usuarioId")

                val pagoViewModel = hiltViewModel<PagoViewModel>()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        Log.d("PagoNuevo", "Inicializando PagoViewModel con usuarioId: $usuarioId")
                        pagoViewModel.inicializar(usuarioId)
                    }
                }

                if (usuarioId == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Usuario no autenticado")
                    }
                } else {
                    PagoScreen(
                        viewModel = pagoViewModel,
                        pagoParaEditar = null,
                        usuarioId = usuarioId,
                        onGuardar = { monto, categoriaId, frecuencia, fechaInicio, fechaFin, usuarioIdGuardado ->
                            pagoViewModel.crearPagoRecurrente(
                                PagoRecurrenteDto(
                                    monto = monto,
                                    categoriaId = categoriaId,
                                    frecuencia = frecuencia,
                                    fechaInicio = fechaInicio,
                                    fechaFin = fechaFin,
                                    usuarioId = usuarioIdGuardado
                                )
                            )
                            navHostController.navigate("pagos/{usuarioId}") {
                                popUpTo("pagos/{usuarioId}") { inclusive = true }
                            }
                        },
                        onCancel = {
                            navHostController.popBackStack()
                        }
                    )
                }
            }


            composable(
                route = "pago_detalle/{usuarioId}/{pagoId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("pagoId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                val pagoId = backStackEntry.arguments?.getInt("pagoId") ?: 0
                Log.d("PagoDetalle", "usuarioId=$usuarioId, pagoId=$pagoId")

                val pagoViewModel = hiltViewModel<PagoViewModel>()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        pagoViewModel.cargarPagosRecurrentes(usuarioId)
                        pagoViewModel.fetchCategorias(usuarioId)
                    }
                }

                val uiState by pagoViewModel.uiState.collectAsState()
                val categorias by pagoViewModel.categorias.collectAsState()

                val pago = uiState.pagos.find { it.pagoRecurrenteId == pagoId }
                if (pago != null) {
                    val categoria = categorias.find { it.categoriaId == pago.categoriaId }
                    val categoriaIcono = categoria?.icono ?: "💵"
                    val categoriaNombre = categoria?.nombre ?: "Sin categoría"

                    PagoDetalleScreen(
                        pagoId = pagoId,
                        pago = pago,
                        categoriaIcono = categoriaIcono,
                        categoriaNombre = categoriaNombre,
                        onBackClick = { navHostController.popBackStack() },
                        onEditarClick = {
                            navHostController.navigate("pago_editar/$usuarioId/$pagoId")
                        },
                        onEliminarClick = {
                            // si tienes algo para eliminar aquí
                        },
                        onEliminarConfirmado = {
                            pagoViewModel.eliminarPagoRecurrente(pagoId)
                        },
                        navHostController = navHostController,
                        pagoViewModel = pagoViewModel
                    )
                }
            }


            composable(
                route = "pago_editar/{usuarioId}/{pagoId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("pagoId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                val pagoId = backStackEntry.arguments?.getInt("pagoId") ?: 0
                Log.d("PagoEditar", "usuarioId=$usuarioId, pagoId=$pagoId")

                val pagoViewModel = hiltViewModel<PagoViewModel>()
                val uiState by pagoViewModel.uiState.collectAsState()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        pagoViewModel.cargarPagosRecurrentes(usuarioId)
                        pagoViewModel.fetchCategorias(usuarioId)
                        Log.d(
                            "LimiteEditar",
                            "Cargando límites y categorías para usuario $usuarioId"
                        )
                    }
                }

                val pago = uiState.pagos.find { it.pagoRecurrenteId == pagoId }

                if (pago != null) {
                    PagoScreen(
                        viewModel = pagoViewModel,
                        pagoParaEditar = pago,
                        usuarioId = usuarioId,
                        onGuardar = { monto, categoriaId, frecuencia, fechaInicio, fechaFin, usuarioIdGuardado ->
                            val pagoActualizado = PagoRecurrenteDto(
                                pagoRecurrenteId = pagoId,
                                monto = monto,
                                categoriaId = categoriaId,
                                frecuencia = frecuencia,
                                fechaInicio = fechaInicio,
                                fechaFin = fechaFin,
                                usuarioId = usuarioIdGuardado
                            )
                            pagoViewModel.actualizarPagoRecurrente(pagoId, pagoActualizado)
                            navHostController.navigate("pagos/{usuarioId}") {
                                popUpTo("pagos/{usuarioId}") { inclusive = true }
                            }
                        },
                        onCancel = { navHostController.popBackStack() }
                    )
                }
            }


            composable("limites/{usuarioId}") {
                val limiteViewModel = hiltViewModel<LimiteViewModel>()
                val gastoViewModel = hiltViewModel<GastoViewModel>()

                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        limiteViewModel.cargarLimites(usuarioId)
                        limiteViewModel.fetchCategorias(usuarioId)
                        gastoViewModel.inicializar(usuarioId)
                    }
                }

                LimiteListScreen(
                    viewModel = limiteViewModel,
                    gastoViewModel = gastoViewModel,
                    onAgregarLimiteClick = {
                        navHostController.navigate("limite_nuevo/$usuarioId")
                    },
                    onBackClick = {
                        navHostController.popBackStack()
                    },
                    onLimiteClick = { limiteId ->
                        navHostController.navigate("limite_detalle/$usuarioId/$limiteId")
                    }
                )
            }


            composable(
                "limite_nuevo/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                Log.d("LimiteNuevo", "usuarioId recibido en ruta: $usuarioId")
                val limiteViewModel = hiltViewModel<LimiteViewModel>()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        Log.d(
                            "LimiteNuevo",
                            "Inicializando LimiteViewModel con usuarioId: $usuarioId"
                        )
                        limiteViewModel.inicializar(usuarioId)
                    }
                }

                if (usuarioId == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Usuario no autenticado")
                    }
                } else {
                    LimiteScreen(
                        viewModel = limiteViewModel,
                        limiteParaEditar = null,
                        onGuardar = { montoLimite, categoriaId, periodo, usuarioIdGuardado ->
                            limiteViewModel.crearLimite(
                                LimiteGastoDto(
                                    montoLimite = montoLimite,
                                    categoriaId = categoriaId,
                                    periodo = periodo,
                                    usuarioId = usuarioIdGuardado
                                )
                            )
                            navHostController.navigate("limites/$usuarioId") {
                                popUpTo("limites/$usuarioId") { inclusive = true }
                            }
                        },
                        onCancel = { navHostController.popBackStack() },
                        usuarioId = usuarioId
                    )
                }
            }

            composable(
                route = "limite_detalle/{usuarioId}/{limiteId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("limiteId") { type = NavType.IntType }
                )
            ) { backStackEntry ->

                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                val limiteId = backStackEntry.arguments?.getInt("limiteId") ?: 0
                Log.d("LimiteDetalle", "usuarioId=$usuarioId, limiteId=$limiteId")

                val limiteViewModel = hiltViewModel<LimiteViewModel>()
                val gastoViewModel = hiltViewModel<GastoViewModel>()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        limiteViewModel.cargarLimites(usuarioId)
                        limiteViewModel.fetchCategorias(usuarioId)
                        gastoViewModel.inicializar(usuarioId)
                    }
                }

                val uiState by limiteViewModel.uiState.collectAsState()
                val categorias by limiteViewModel.categorias.collectAsState()

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val limite = uiState.limites.find { it.limiteGastoId == limiteId }
                    if (limite != null) {
                        val categoria = categorias.find { it.categoriaId == limite.categoriaId }
                        val categoriaIcono = categoria?.icono ?: "💵"
                        val categoriaNombre = categoria?.nombre ?: "Sin categoría"

                        LimiteDetalleScreen(
                            limite = limite,
                            categoriaIcono = categoriaIcono,
                            categoriaNombre = categoriaNombre,
                            gastoViewModel = gastoViewModel,
                            onBackClick = { navHostController.popBackStack() },
                            onEditarClick = { navHostController.navigate("limite_editar/$usuarioId/$limiteId") },
                            onEliminarClick = {  },
                            onEliminarConfirmado = {
                                limiteViewModel.eliminarLimite(limiteId)
                                navHostController.navigate("limites/$usuarioId") {
                                    popUpTo("limites/$usuarioId") { inclusive = true }
                                }
                            }
                        )
                    } else {
                        Log.e(
                            "LimiteDetalle",
                            "No se encontró límite con id=$limiteId para usuario=$usuarioId"
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Límite no encontrado")
                        }
                    }
                }
            }

            composable(
                route = "limite_editar/{usuarioId}/{limiteId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("limiteId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                val limiteId = backStackEntry.arguments?.getInt("limiteId") ?: 0
                Log.d("LimiteEditar", "usuarioId=$usuarioId, limiteId=$limiteId")

                val limiteViewModel = hiltViewModel<LimiteViewModel>()
                val uiState by limiteViewModel.uiState.collectAsState()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        limiteViewModel.cargarLimites(usuarioId)
                        limiteViewModel.fetchCategorias(usuarioId)
                        Log.d(
                            "LimiteEditar",
                            "Cargando límites y categorías para usuario $usuarioId"
                        )
                    }
                }

                val limite = uiState.limites.find { it.limiteGastoId == limiteId }

                if (limite != null) {
                    LimiteScreen(
                        viewModel = limiteViewModel,
                        limiteParaEditar = limite,
                        onGuardar = { montoLimite, categoriaId, periodo, usuarioIdGuardado ->
                            val limiteActualizado = LimiteGastoDto(
                                limiteGastoId = limiteId,
                                montoLimite = montoLimite,
                                categoriaId = categoriaId,
                                periodo = periodo,
                                usuarioId = usuarioIdGuardado
                            )
                            limiteViewModel.actualizarLimite(limiteId, limiteActualizado)
                            navHostController.navigate("limites/$usuarioId") {
                                popUpTo("limites/$usuarioId") { inclusive = true }
                            }
                        },
                        onCancel = { navHostController.popBackStack() },
                        usuarioId = usuarioId
                    )
                }
            }


            composable(
                route = "metaahorros/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                val metaViewModel = hiltViewModel<MetaViewModel>()

                LaunchedEffect(usuarioId) {
                    metaViewModel.cargarMetas(usuarioId)
                }


                MetaListScreen(
                    viewModel = metaViewModel,
                    usuarioId = usuarioId,
                    navController = navHostController,
                    onBackClick = { navHostController.popBackStack() },
                    onAgregarMetaClick = {
                        navHostController.navigate("meta_nueva/$usuarioId")
                    },
                    onMetaClick = { metaId ->
                        navHostController.navigate("meta_detalle/$usuarioId/$metaId")
                    },
                    onAgregarMontoClick = { metaId ->
                        navHostController.navigate("meta_monto_ahorro/$usuarioId/$metaId")
                    }
                )
            }

            composable(
                route = "meta_nueva/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                val metaViewModel = hiltViewModel<MetaViewModel>()

                MetaScreen(
                    metaParaEditar = null,
                    usuarioId = usuarioId,
                    onGuardar = { nombre, montoObjetivo, fechaFinal, contribucion, imagen, usuarioId ->
                        metaViewModel.crearMeta(
                            MetaAhorroDto(
                                nombreMeta = nombre,
                                montoObjetivo = montoObjetivo,
                                fechaFinalizacion = fechaFinal,
                                contribucionRecurrente = if (contribucion) 0.0 else null,
                                imagen = imagen,
                                montoAhorrado = 0.0,
                                fechaMontoAhorrado = fechaFinal,
                                usuarioId = usuarioId
                            ),
                            usuarioId = usuarioId
                        )
                        navHostController.navigate("metaahorros/$usuarioId") {
                            popUpTo("metaahorros/$usuarioId") { inclusive = true }
                        }
                    },
                    onCancel = { navHostController.popBackStack() },
                    onImagenSeleccionada = {  },
                )
            }


            composable(
                route = "meta_detalle/{usuarioId}/{metaId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("metaId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }
                val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0
                val metaViewModel = hiltViewModel<MetaViewModel>()
                val uiState by metaViewModel.uiState.collectAsState()
                val meta = uiState.metaSeleccionada

                LaunchedEffect(usuarioId, metaId) {
                    metaViewModel.cargarMetas(usuarioId, metaId)
                }

                meta?.let {
                    MetaDetalleScreen(
                        meta = it,
                        onBackClick = { navHostController.popBackStack() },
                        onEditarClick = {
                            navHostController.navigate("meta_editar/$usuarioId/$metaId")
                        },
                        onEliminarClick = {  },
                        onEliminarConfirmado = {
                            metaViewModel.eliminarMeta(metaId)
                            navHostController.navigate("metaahorros/$usuarioId") {
                                popUpTo("metaahorros/$usuarioId") { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(
                route = "meta_editar/{usuarioId}/{metaId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("metaId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0
                val metaViewModel = hiltViewModel<MetaViewModel>()
                val uiState by metaViewModel.uiState.collectAsState()
                val meta = uiState.metaSeleccionada
                LaunchedEffect(usuarioId, metaId) {
                    metaViewModel.setUsuarioId(usuarioId)
                    metaViewModel.cargarMetas(usuarioId, metaId)
                }

                meta?.let {
                    MetaScreen(
                        metaParaEditar = it,
                        usuarioId = usuarioId,
                        onGuardar = { nombre, montoObjetivo, fechaFinal, contribucion, imagen, _ ->
                            val metaActualizada = it.copy(
                                nombreMeta = nombre,
                                montoObjetivo = montoObjetivo,
                                fechaFinalizacion = fechaFinal,
                                contribucionRecurrente = if (contribucion) 0.0 else null,
                                imagen = imagen,
                                usuarioId = usuarioId
                            )
                            metaViewModel.actualizarMeta(metaId, metaActualizada)
                            navHostController.navigate("metaahorros/$usuarioId") {
                                popUpTo("metaahorros/$usuarioId") { inclusive = true }
                            }
                        },
                        onCancel = { navHostController.popBackStack() },
                        onImagenSeleccionada = {  }
                    )
                }
            }

            composable(
                route = "meta_monto_ahorro/{usuarioId}/{metaId}",
                arguments = listOf(
                    navArgument("usuarioId") { type = NavType.IntType },
                    navArgument("metaId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }
                val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0

                val metaViewModel = hiltViewModel<MetaViewModel>()

                val uiState by metaViewModel.uiState.collectAsState()
                LaunchedEffect(usuarioId, metaId) {
                    metaViewModel.setUsuarioId(usuarioId)
                    metaViewModel.cargarMetas(usuarioId, metaId)
                }


                val meta = metaViewModel.obtenerMetas(metaId) ?: MetaAhorroDto(
                    metaAhorroId = 0,
                    nombreMeta = "",
                    montoObjetivo = 0.0,
                    fechaFinalizacion = OffsetDateTime.now(),
                    usuarioId = usuarioId
                )

                MetaMAhorroScreen(
                    meta = meta,
                    onGuardarMonto = { montoAhorrado, fechaMonto ->
                        metaViewModel.actualizarMontoAhorrado(meta.metaAhorroId, montoAhorrado, fechaMonto)
                        navHostController.popBackStack()
                    },
                    onCancel = {
                        navHostController.popBackStack()
                    }
                )
            }


            composable(
                route = "chatIA/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                ChatIaScreen(
                    navController = navHostController,
                    usuarioId = usuarioId
                )
            }

            composable("cambiarFoto/{usuarioId}") { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toInt() ?: 0
                CambiarFotoScreen(
                    usuarioId = usuarioId,
                    onNavigateBack = { navHostController.popBackStack() }
                )
            }


            composable("ajustes/{usuarioId}") { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }
                val scope = rememberCoroutineScope()

                AjustesListScreen(
                    navController = navHostController,
                    usuarioId = usuarioId,
                    onEditarPerfil = {
                        navHostController.navigate("panel_usuario/$usuarioId")
                    },
                    onCambiarContrasena = {
                        navHostController.navigate("cambiar_contrasena/$usuarioId")
                    },
                    onCerrarSesion = {
                        scope.launch {
                            DataLogin.limpiarSesion(context)
                            navHostController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onNotificaciones = {
                        navHostController.navigate("notificaciones/$usuarioId")
                    },
                    onApariencia = {
                        navHostController.navigate("apariencia/$usuarioId")
                    },
                    onCentroAyuda = {
                        navHostController.navigate("centro_ayuda")
                    },
                    onSoporte = {
                        navHostController.navigate("soporte")
                    }
                )
            }

            composable(
                route = "notificaciones/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                NotificacionesScreen(
                    navController = navHostController,
                    usuarioId = usuarioId
                )
            }

            composable(
                route = "apariencia/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val context = LocalContext.current
                val usuarioId by produceState(initialValue = 0) {
                    value = DataLogin.obtenerUsuarioId(context) ?: 0
                }

                AparienciaScreen(
                    navController = navHostController,
                    usuarioId = usuarioId
                )
            }

            composable("cambiar_contrasena/{usuarioId}") { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0

                CambiarContrasenaScreen(
                    usuarioId = usuarioId,
                    onBack = {
                        navHostController.popBackStack()
                    }
                )
            }

            composable("panel_usuario/{usuarioId}") { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0

                PanelUsuarioScreen(
                    navController = navHostController,
                    usuarioId = usuarioId,
                    onCambiarContrasenaClick = {
                        navHostController.navigate("cambiar_contrasena/$usuarioId") {
                            launchSingleTop = true
                        }
                    },
                    onCambiarFoto = {
                        navHostController.navigate("cambiarFoto/$usuarioId")                    },
                    onDivisa = {
                        // Implementar lógica para cambiar divisa
                    },
                    onAjustes = {
                        navHostController.navigate("ajustes/$usuarioId")
                    },
                    onTransacciones = {
                        navHostController.navigate("gastos")
                    }
                )
            }

            composable(
                route = "grafico/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                Log.d("GraficoComposable", "usuarioId recibido: $usuarioId")

                val gastoViewModel = hiltViewModel<GastoViewModel>()

                LaunchedEffect(usuarioId) {
                    if (usuarioId != 0) {
                        gastoViewModel.inicializar(usuarioId)
                    }
                }

                if (usuarioId != 0) {
                    GraficoScreen(
                        usuarioId = usuarioId,
                        gastoviewModel = hiltViewModel(),
                        onBackClick = {
                            navHostController.popBackStack()
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando usuario...")
                    }
                }
            }



        }

    }

}

suspend fun verificarSesion(context: Context): Boolean {
    val usuarioId = DataLogin.obtenerUsuarioId(context)
    return usuarioId != null && usuarioId != 0
}
