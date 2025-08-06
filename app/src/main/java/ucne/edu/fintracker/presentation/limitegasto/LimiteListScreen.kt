package ucne.edu.fintracker.presentation.limitegasto

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucne.edu.fintracker.presentation.gasto.GastoViewModel
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.DayOfWeek

@Composable
fun LimiteListScreen(
    viewModel: LimiteViewModel,
    gastoViewModel: GastoViewModel,
    onBackClick: () -> Unit,
    onAgregarLimiteClick: () -> Unit,
    onLimiteClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val gastoUiState by gastoViewModel.uiState.collectAsState()
    val transacciones = gastoUiState.transacciones
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Límites de Gastos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarLimiteClick,
                containerColor = Color(0xFF8BC34A)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Límite",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    if (uiState.limites.isEmpty()) {
                        Text(
                            text = "No hay límites de gasto.",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.limites) { limite ->
                                val categoria = categorias.find { it.categoriaId == limite.categoriaId }
                                val nombreCategoria = categoria?.nombre ?: "Sin categoría"
                                val icono = categoria?.icono ?: "❓"
                                val colorFondo = try {
                                    Color(android.graphics.Color.parseColor(categoria?.colorFondo ?: "#8BC34A"))
                                } catch (e: Exception) {
                                    Color(0xFF8BC34A)
                                }
                                val totalGastado = remember(transacciones, limite.categoriaId, limite.periodo) {
                                    calcularTotalGastadoPorPeriodo(transacciones, limite.categoriaId, limite.periodo)
                                }
                                val porcentaje = if (limite.montoLimite > 0) {
                                    ((totalGastado / limite.montoLimite) * 100).coerceAtMost(100.0)
                                } else 0.0

                                val excedePresupuesto = totalGastado > limite.montoLimite

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onLimiteClick(limite.limiteGastoId) }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(colorFondo, shape = MaterialTheme.shapes.small),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = icono,
                                                fontSize = 24.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = nombreCategoria,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Límite: RD$ ${String.format("%.2f", limite.montoLimite)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Gastado: RD$ ${String.format("%.2f", totalGastado)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (excedePresupuesto) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(120.dp)
                                                .height(12.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                                                )
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth((porcentaje / 100f).toFloat().coerceAtMost(1f))
                                                    .background(
                                                        color = when {
                                                            excedePresupuesto -> Color.Red
                                                            porcentaje >= 80 -> Color(0xFFFF9800)
                                                            porcentaje >= 60 -> Color(0xFFFFC107)
                                                            else -> Color(0xFF4CAF50)
                                                        },
                                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                                                    )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "${porcentaje.toInt()}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (excedePresupuesto) Color.Red else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (excedePresupuesto) FontWeight.Bold else FontWeight.Normal
                                        )

                                        if (excedePresupuesto) {
                                            Text(
                                                text = "Excedido",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Red,
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