package ucne.edu.fintracker.presentation.limitegasto

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimiteScreen(
    viewModel: LimiteViewModel,
    usuarioId: Int,
    limiteParaEditar: LimiteGastoDto? = null,
    onGuardar: (Double, Int, String,  usuarioId: Int) -> Unit,
    onCancel: () -> Unit
) {
    val categorias by viewModel.categorias.collectAsState()
    val context = LocalContext.current

    var categoriaSeleccionada by remember { mutableStateOf<CategoriaDto?>(null) }
    var montoLimite by remember { mutableStateOf(limiteParaEditar?.montoLimite?.toString() ?: "") }
    var periodoSeleccionado by remember { mutableStateOf(limiteParaEditar?.periodo ?: "") }

    val periodos = listOf("Diario", "Semanal", "Quincenal", "Mensual", "Anual")

    LaunchedEffect(categorias, limiteParaEditar) {
        if (limiteParaEditar != null && categorias.isNotEmpty()) {
            categoriaSeleccionada = categorias.find { it.categoriaId == limiteParaEditar.categoriaId }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (limiteParaEditar == null) "Nuevo Límite" else "Editar Límite",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    categoriaSeleccionada?.let { cat ->
                        if (montoLimite.isNotBlank() && periodoSeleccionado.isNotBlank()) {
                            onGuardar(
                                montoLimite.toDoubleOrNull() ?: 0.0,
                                cat.categoriaId,
                                periodoSeleccionado,
                                usuarioId
                            )

                            if (limiteParaEditar == null) {
                                montoLimite = ""
                                categoriaSeleccionada = null
                                periodoSeleccionado = ""
                            }
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    } ?: Toast.makeText(context, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
            ) {
                Text(if (limiteParaEditar == null) "Agregar Límite" else "Guardar Cambios")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            var expandedCategoria by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                categoriaSeleccionada = cat
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = montoLimite,
                onValueChange = { montoLimite = it },
                label = { Text("Monto del Límite") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            var expandedPeriodo by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedPeriodo,
                onExpandedChange = { expandedPeriodo = !expandedPeriodo }
            ) {
                OutlinedTextField(
                    value = periodoSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Período") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPeriodo)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPeriodo,
                    onDismissRequest = { expandedPeriodo = false }
                ) {
                    periodos.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p) },
                            onClick = {
                                periodoSeleccionado = p
                                expandedPeriodo = false
                            }
                        )
                    }
                }
            }
        }
    }
}
