package ucne.edu.fintracker.presentation.gasto

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import ucne.edu.fintracker.presentation.categoria.CategoriaViewModel
import ucne.edu.fintracker.presentation.components.MenuScreen
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.platform.LocalContext
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import java.util.Locale


@Composable
fun ToggleTextButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (isSelected) Color.White else Color.Black,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) Color(0xFF8BC34A) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun GastoPieChart(
    transacciones: List<TransaccionDto>,
    categorias: List<CategoriaDto>,
    modifier: Modifier = Modifier
        .size(200.dp)
        .padding(16.dp)
) {
    if (transacciones.isEmpty()) return

    val total = transacciones.sumOf { it.monto }
    val transaccionesPorCategoria = transacciones.groupBy { it.categoriaId }

    val categoriaColores = categorias.associate { categoria ->
        val hexColor = categoria.colorFondo.removePrefix("#")
        val colorInt = try {
            android.graphics.Color.parseColor("#$hexColor")
        } catch (e: IllegalArgumentException) {
            android.graphics.Color.GRAY
        }
        categoria.categoriaId to Color(colorInt)
    }



    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            var startAngle = -90f

            transaccionesPorCategoria.forEach { (categoriaId, lista) ->
                val sweep = (lista.sumOf { it.monto } / total * 360f).toFloat()
                drawArc(
                    color = categoriaColores[categoriaId] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }

            drawCircle(
                color = Color.White,
                radius = radius * 0.6f
            )
        }

        Text(
            text = "${"%,.2f".format(total)} RD$",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoListScreen(
    viewModel: GastoViewModel,
    usuarioId: Int,
    categoriaViewModel: CategoriaViewModel,
    navController: NavController,
    onNuevoClick: () -> Unit
) {
    val categoriaState by categoriaViewModel.uiState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val transaccionesFiltradas by viewModel.transaccionesFiltradas.collectAsState()

    val total = transaccionesFiltradas.sumOf { it.monto }

    var tipo = state.tipoSeleccionado

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MenuScreen(
        drawerState = drawerState,
        navController = navController,
        usuarioId = usuarioId,
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {

                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("panel_usuario/$usuarioId") }) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navController.navigate("gasto_nuevo/$tipo/$usuarioId") },
                        containerColor = Color(0xFF8BC34A),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nuevo", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            NavigationBarItem(
                                selected = true,
                                onClick = { navController.navigate("gastos") },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate("chatIA/$usuarioId") },
                                icon = { Icon(Icons.Default.Assistant, contentDescription = "IA Asesor") },
                                label = { Text("IA Asesor") }
                            )
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            NavigationBarItem(
                                selected = currentRoute == "metaahorros/$usuarioId",
                                onClick = {
                                    navController.navigate("metaahorros/$usuarioId") {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                    }
                                },
                                icon = { Icon(Icons.Default.Star, contentDescription = "Metas") },
                                label = { Text("Metas") }
                            )
                        }

                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.cambiarTipo("Gasto") },
                            colors = if (tipo == "Gasto")
                                ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                            else
                                ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Gastos", fontSize = 12.sp, color = if (tipo == "Gasto") Color.White else Color.Black)
                        }

                        Column(
                            modifier = Modifier.weight(2f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Balance", fontSize = 14.sp, color = Color.Gray)
                            Text(
                                text = "%,.0f RD$".format(total),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.cambiarTipo("Ingreso") },
                            colors = if (tipo == "Ingreso")
                                ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                            else
                                ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Ingresos", fontSize = 12.sp, color = if (tipo == "Ingreso") Color.White else Color.Black)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0F0F0), RoundedCornerShape(24.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("Día", "Semana", "Mes", "Año").forEach { filtro ->
                            ToggleTextButton(
                                text = filtro,
                                isSelected = state.filtro == filtro,
                                onClick = { viewModel.cambiarFiltro(filtro) }
                            )
                        }
                    }

                    val contexto = LocalContext.current
                    val seleccion = state.filtro

                    val fechaActual = OffsetDateTime.now()
                    val fechaTexto = when (seleccion) {
                        "Día" -> "${fechaActual.dayOfMonth} ${fechaActual.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }} ${fechaActual.year}"
                        "Semana" -> {
                            val inicioSemana = fechaActual.with(DayOfWeek.MONDAY)
                            val finSemana = inicioSemana.plusDays(6)

                            val formatter = DateTimeFormatter.ofPattern("d 'de' MMM", Locale("es"))
                            val inicioStr = inicioSemana.format(formatter).lowercase()
                            val finStr = finSemana.format(formatter).lowercase()

                            "$inicioStr al $finStr"
                        }
                        "Mes" -> "${fechaActual.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }} ${fechaActual.year}"
                        "Año" -> "${fechaActual.year}"
                        else -> ""
                    }

                    Text(
                        text = fechaTexto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )


                    if (transaccionesFiltradas.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val outerRadius = size.minDimension / 2
                                val innerRadius = outerRadius * 0.6f

                                drawCircle(color = Color(0xFFB0BEC5), radius = outerRadius)

                                drawContext.canvas.nativeCanvas.apply {
                                    val paint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.DKGRAY
                                        style = android.graphics.Paint.Style.STROKE
                                        strokeWidth = 8f
                                        pathEffect = android.graphics.DashPathEffect(
                                            floatArrayOf(10f, 10f),
                                            0f
                                        )
                                        isAntiAlias = true
                                    }
                                    drawCircle(center.x, center.y, innerRadius, paint)
                                }
                            }

                            val mensajeNoHay = when (state.filtro) {
                                "Día" -> if (tipo == "Gasto") "No hubo gastos este día" else "No hubo ingresos este día"
                                "Semana" -> if (tipo == "Gasto") "No hubo gastos esta semana" else "No hubo ingresos esta semana"
                                "Mes" -> if (tipo == "Gasto") "No hubo gastos este mes" else "No hubo ingresos este mes"
                                "Año" -> if (tipo == "Gasto") "No hubo gastos este año" else "No hubo ingresos este año"
                                else -> "No hay datos"
                            }

                            Text(
                                text = mensajeNoHay,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        GastoPieChart(
                            transacciones = transaccionesFiltradas,
                            categorias = categoriaState.categorias,
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        when {
                            state.isLoading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color(0xFF8BC34A)
                                )
                            }

                            state.error != null -> {
                                Text(
                                    text = state.error ?: "Error desconocido",
                                    color = Color.Red,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                            transaccionesFiltradas.isEmpty() -> {
                                Text(
                                    text = "No hay transacciones.",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.Gray
                                )
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(transaccionesFiltradas) { transaccion ->

                                        val categoria = categoriaState.categorias.find {
                                            it.categoriaId == transaccion.categoriaId
                                        }

                                        val colorFondo = try {
                                            Color(android.graphics.Color.parseColor("#${categoria?.colorFondo?.removePrefix("#") ?: "CCCCCC"}"))
                                        } catch (e: Exception) {
                                            Color.Gray
                                        }

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    navController.navigate("gasto_detalle/$usuarioId/${transaccion.transaccionId}")
                                                },
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {

                                                    Box(
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .clip(CircleShape)
                                                            .background(colorFondo),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = categoria?.icono ?: "❓",
                                                            fontSize = 18.sp
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.width(12.dp))

                                                    Column {
                                                        Text(
                                                            text = categoria?.nombre ?: "Desconocida",
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            transaccion.fecha.format(
                                                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                                            ),
                                                            color = Color.Gray
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = "%,.2f RD$".format(transaccion.monto),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
