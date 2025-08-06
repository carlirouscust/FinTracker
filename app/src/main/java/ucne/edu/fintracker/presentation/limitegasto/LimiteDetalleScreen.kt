package ucne.edu.fintracker.presentation.limitegasto

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import ucne.edu.fintracker.presentation.gasto.GastoViewModel
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimiteDetalleScreen(
    limite: LimiteGastoDto,
    categoriaIcono: String,
    categoriaNombre: String,
    gastoViewModel: GastoViewModel,
    onBackClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onEliminarConfirmado: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    val uiState by gastoViewModel.uiState.collectAsState()
    val transacciones = uiState.transacciones

    val totalGastado = remember(transacciones, limite.categoriaId, limite.periodo) {
        calcularTotalGastadoPorPeriodo(transacciones, limite.categoriaId, limite.periodo)
    }

    val porcentaje = if (limite.montoLimite > 0) {
        ((totalGastado / limite.montoLimite) * 100).coerceAtMost(100.0)
    } else 0.0

    val excedePresupuesto = totalGastado > limite.montoLimite

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Límite") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(
                                if (limite.montoLimite > 0)
                                    (porcentaje / 100f).toFloat().coerceAtMost(1f)
                                else 0f
                            )
                            .background(
                                color = when {
                                    excedePresupuesto -> Color.Red
                                    porcentaje >= 80 -> Color(0xFFFF9800)
                                    porcentaje >= 60 -> Color(0xFFFFC107)
                                    else -> Color(0xFF4CAF50)
                                },
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            )
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${porcentaje.toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (excedePresupuesto) "Presupuesto excedido" else "Presupuesto consumido",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (excedePresupuesto) Color.Red else MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "RD$ ${String.format("%.2f", totalGastado)} / RD$ ${String.format("%.2f", limite.montoLimite)}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (excedePresupuesto) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Excedido por RD$ ${String.format("%.2f", totalGastado - limite.montoLimite)}",
                        fontSize = 14.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = categoriaIcono.takeIf { it.isNotBlank() } ?: "💵",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = categoriaNombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Divider()

            Text("Período", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(limite.periodo, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider()

            Text("Monto Límite", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("RD$ ${String.format("%.2f", limite.montoLimite)}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider()

            Text("Total Gastado", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(
                "RD$ ${String.format("%.2f", totalGastado)}",
                fontSize = 16.sp,
                color = if (excedePresupuesto) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider()

            val cantidadTransacciones = remember(transacciones, limite.categoriaId, limite.periodo) {
                contarTransaccionesPorPeriodo(transacciones, limite.categoriaId, limite.periodo)
            }

            Text("Transacciones", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(
                "$cantidadTransacciones transacciones de gasto",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider()

            Spacer(modifier = Modifier.weight(1f))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onEditarClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar", color = MaterialTheme.colorScheme.onPrimary)
                }

                Button(
                    onClick = { mostrarDialogoEliminar = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.onError)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar este límite de gasto?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        onEliminarConfirmado()
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminar = false }
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}

private fun calcularTotalGastadoPorPeriodo(
    transacciones: List<TransaccionDto>,
    categoriaId: Int,
    periodo: String
): Double {
    val ahora = OffsetDateTime.now()
    return transacciones
        .filter { transaccion ->
            transaccion.tipo.trim().equals("Gasto", ignoreCase = true) &&
                    transaccion.categoriaId == categoriaId &&
                    estaEnPeriodo(transaccion.fecha, periodo, ahora)
        }
        .sumOf { it.monto }
}

private fun contarTransaccionesPorPeriodo(
    transacciones: List<TransaccionDto>,
    categoriaId: Int,
    periodo: String
): Int {
    val ahora = OffsetDateTime.now()

    return transacciones
        .filter { transaccion ->
            transaccion.tipo.trim().equals("Gasto", ignoreCase = true) &&
                    transaccion.categoriaId == categoriaId &&
                    estaEnPeriodo(transaccion.fecha, periodo, ahora)
        }
        .size
}

private fun estaEnPeriodo(
    fechaTransaccion: OffsetDateTime,
    periodo: String,
    fechaReferencia: OffsetDateTime
): Boolean {
    return when (periodo.lowercase()) {
        "diario" -> {
            val hoy = fechaReferencia.toLocalDate()
            fechaTransaccion.toLocalDate().isEqual(hoy)
        }
        "semanal" -> {
            val lunes = fechaReferencia.toLocalDate().with(DayOfWeek.MONDAY)
            val domingo = fechaReferencia.toLocalDate().with(DayOfWeek.SUNDAY)
            val fecha = fechaTransaccion.toLocalDate()
            !fecha.isBefore(lunes) && !fecha.isAfter(domingo)
        }
        "mensual" -> {
            val fecha = fechaTransaccion.toLocalDate()
            val referencia = fechaReferencia.toLocalDate()
            fecha.month == referencia.month && fecha.year == referencia.year
        }
        "anual" -> {
            fechaTransaccion.toLocalDate().year == fechaReferencia.toLocalDate().year
        }
        else -> false
    }
}