package ucne.edu.fintracker.presentation.gasto

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import ucne.edu.fintracker.presentation.limitegasto.LimiteViewModel
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoScreen(
    categorias: List<CategoriaDto>,
    usuarioId: Int,
    transaccionParaEditar: TransaccionDto? = null,
    tipoInicial: String = "Gasto",
    onGuardar: (tipo: String, monto: Double, categoriaNombre: String, fecha: String, notas: String, usuarioId: Int) -> Unit,
    onCancel: () -> Unit
) {
    var tipo by remember { mutableStateOf(transaccionParaEditar?.tipo ?: tipoInicial) }
    var monto by remember {
        mutableStateOf(TextFieldValue(transaccionParaEditar?.monto?.toString() ?: ""))
    }

    var expandedCategoria by remember { mutableStateOf(false) }
    var fechaSeleccionada by remember {
        mutableStateOf(transaccionParaEditar?.fecha.toString() ?: "Hoy") }

    var notas by remember { mutableStateOf(transaccionParaEditar?.notas ?: "") }

    var categoriaSeleccionada by remember {
        mutableStateOf<CategoriaDto?>(null)
    }
    val fechaFormato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    LaunchedEffect(transaccionParaEditar, categorias) {
        if (transaccionParaEditar != null) {
            categoriaSeleccionada = categorias.find { it.categoriaId == transaccionParaEditar.categoriaId }

            fechaSeleccionada = transaccionParaEditar.fecha.format(fechaFormato)
        } else {
            categoriaSeleccionada = null
            fechaSeleccionada = fechaFormato.format(OffsetDateTime.now())
        }
    }


    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (transaccionParaEditar == null) "Agregar Transacción" else "Editar Transacción",
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { tipo = "Gasto" },
                    colors = if (tipo == "Gasto")
                        ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                    else
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gasto", color = if (tipo == "Gasto") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = { tipo = "Ingreso" },
                    colors = if (tipo == "Ingreso")
                        ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                    else
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text("Ingreso", color = if (tipo == "Ingreso") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            OutlinedTextField(
                value = monto,
                onValueChange = {
                    if (it.text.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        monto = it
                    }
                },
                label = { Text("Monto") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            monto = monto.copy(selection = TextRange(0, monto.text.length))
                        }
                        if (!focusState.isFocused) {
                            val numero = monto.text.toDoubleOrNull() ?: 0.0
                            val redondeado = String.format("%.2f", numero)
                            monto = TextFieldValue(redondeado, TextRange(redondeado.length))
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            val categoriasFiltradas = categorias.filter { it.tipo.equals(tipo, ignoreCase = true) }

            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "Seleccionar categoría",
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categoriasFiltradas.forEach { cat ->
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

            // Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Hoy", "Ayer").forEach { dia ->
                    val selected = fechaSeleccionada == dia
                    Button(
                        onClick = { fechaSeleccionada = dia },
                        colors = if (selected)
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        else
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(dia, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                IconButton(
                    onClick = {
                        val calendario = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
                            },
                            calendario.get(Calendar.YEAR),
                            calendario.get(Calendar.MONTH),
                            calendario.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Notas
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Button(
                onClick = {
                    val montoDouble = monto.text.toDoubleOrNull() ?: 0.0
                    val categoriaId = categoriaSeleccionada?.categoriaId ?: 0

                    if (montoDouble <= 0.0 || categoriaId == 0) {
                        Toast.makeText(context, "Completa los campos correctamente", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val fecha = when (fechaSeleccionada) {
                        "Hoy" -> OffsetDateTime.now()
                        "Ayer" -> OffsetDateTime.now().minusDays(1)
                        else -> {
                            try {
                                val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
                                val localDate = LocalDate.parse(fechaSeleccionada, formatter)
                                localDate.atStartOfDay().atOffset(ZoneOffset.UTC)
                            } catch (e: Exception) {
                                OffsetDateTime.now()
                            }
                        }
                    }

                    val transaccion = TransaccionDto(
                        transaccionId = transaccionParaEditar?.transaccionId ?: 0,
                        tipo = tipo,
                        monto = montoDouble,
                        categoriaId = categoriaId,
                        fecha = fecha,
                        notas = notas,
                        usuarioId = usuarioId
                    )

                    onGuardar(
                        tipo,
                        montoDouble,
                        categoriaSeleccionada?.nombre ?: "",
                        fechaSeleccionada,
                        notas,
                        usuarioId
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
            ) {
                Text(
                    if (transaccionParaEditar == null) "Guardar" else "Guardar Cambios",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp
                )
            }

        }
    }
}
