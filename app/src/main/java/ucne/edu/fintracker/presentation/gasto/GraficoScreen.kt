package ucne.edu.fintracker.presentation.gasto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ucne.edu.fintracker.presentation.remote.dto.TotalAnual
import ucne.edu.fintracker.presentation.remote.dto.TotalMes

@Composable
fun BarChart(datos: List<TotalMes>, modifier: Modifier = Modifier) {
    val maxTotal = datos.maxOfOrNull { it.total } ?: 1.0
    val barWidth = 24.dp

    Row(
        modifier = modifier.height(150.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        datos.forEach { dato ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .height(((dato.total / maxTotal).toFloat().coerceIn(0f, 1f) * 100).dp)
                        .width(barWidth)
                        .background(Color(0xFF8BC34A), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(dato.mes, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BarChartAnual(datos: List<TotalAnual>, modifier: Modifier = Modifier) {
    val maxTotal = datos.maxOfOrNull { it.total } ?: 1.0
    val barWidth = 24.dp

    Row(
        modifier = modifier.height(150.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        datos.forEach { dato ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .height(((dato.total / maxTotal).toFloat().coerceIn(0f, 1f) * 100).dp)
                        .width(barWidth)
                        .background(Color(0xFF03A9F4), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(dato.ano.toString(), fontSize = 12.sp)
            }
        }
    }
}


@Composable
fun GraficoScreen(
    usuarioId: Int,
    gastoviewModel: GastoViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    val tipoActual by gastoviewModel.uiState.collectAsState()
    val datosMensuales = gastoviewModel.totalesMensuales.value
    val datosAnuales = gastoviewModel.totalesAnuales.value

    LaunchedEffect(usuarioId) {
        gastoviewModel.cambiarTipo("Gasto")
        gastoviewModel.cargarDatos(usuarioId)
    }

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
                        text = "Gráfico de Gasto",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text("Resumen mensual", fontWeight = FontWeight.SemiBold)
            BarChart(datosMensuales, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            Text("Resumen anual", fontWeight = FontWeight.SemiBold)
            BarChartAnual(datosAnuales, modifier = Modifier.fillMaxWidth())
        }
    }
}



