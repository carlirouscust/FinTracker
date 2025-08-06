package ucne.edu.fintracker.presentation.login

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
// import ucne.edu.fintracker.presentation.remote.dto.CambiarPasswordRequest


@Composable
fun ResetPasswordScreen(
    navController: NavController,
    finTrackerApi: FinTrackerApi
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
            }

            Text(
                "Reiniciar Contraseña",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            singleLine = true,
            isError = emailError,
            shape = RoundedCornerShape(14.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,    
                unfocusedTextColor = Color.Black
            )
        )


        if (emailError) {
            Text("El email es obligatorio", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    emailError = true
                    return@Button
                }

                scope.launch {
//                    try {
//
//                        val response = finTrackerApi.enviarLinkResetPassword(CambiarPasswordRequest(email))
//                        if (response.isSuccessful) {
//                            Toast
//                                .makeText(context, "Revisa tu correo para el reinicio", Toast.LENGTH_LONG)
//                                .show()
//                            navController.popBackStack()
//                        } else {
//                            Toast
//                                .makeText(context, "Correo no encontrado o error del servidor", Toast.LENGTH_LONG)
//                                .show()
//                        }
//                    } catch (e: Exception) {
//                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Enviar Link de Reinicio", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("¿Recuerdas tu contraseña?", fontSize = 13.sp, color = Color.Gray)
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Sign In", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
