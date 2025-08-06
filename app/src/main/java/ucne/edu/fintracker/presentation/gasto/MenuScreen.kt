package ucne.edu.fintracker.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp


data class DrawerItem(val label: String, val icon: ImageVector)

val drawerItems = listOf(
    DrawerItem("Inicio", Icons.Default.Home),
    DrawerItem("Gráficos", Icons.Default.PieChart),
    DrawerItem("Pagos Recurrentes", Icons.Default.Schedule),
    DrawerItem("Categorías", Icons.Default.Category),
    DrawerItem("Limite de gastos", Icons.Default.Warning),
    DrawerItem("Ajustes", Icons.Default.Settings)
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    drawerState: DrawerState,
    navController: NavController,
    content: @Composable () -> Unit,
    userName: String = "Sofía Rodriguez",
    usuarioId: Int
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .widthIn(max = 280.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 24.dp, end = 12.dp, bottom = 24.dp)
                ) {
                    Text(
                        text = userName,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                Color(0xFF8BC34A),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .align(Alignment.CenterStart),
                        fontSize = 18.sp
                    )
                }

                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            when (item.label) {
                                "Inicio" -> navController.navigate("gastos")
                                "Gráficos" -> navController.navigate("grafico/$usuarioId")
                                "Pagos Recurrentes" -> navController.navigate("pagos/$usuarioId")
                                "Categorías" -> navController.navigate("categoria/{tipo}")
                                "Limite de gastos" -> navController.navigate("limites/$usuarioId")
                                "Ajustes" -> navController.navigate("ajustes/$usuarioId")
                            }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = content
    )
}

