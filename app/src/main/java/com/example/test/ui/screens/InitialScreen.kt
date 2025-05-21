// Dentro de /app/java/com.example.test/ui/screens/InitialScreen.kt
// (O donde tengas esta pantalla, asegúrate que el package sea el correcto)
package com.example.test.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle // Icono placeholder para Google
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember // Importa remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.R // Asegúrate que esta R sea la de tu proyecto
import com.example.test.data.local.AppDatabase
import com.example.test.data.repository.UserRepository
import com.example.test.ui.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// Define una etiqueta para logs (TAG)
private const val TAG = "InitialScreenGoogleAuth"

// --- COLORES (Mantenemos los que definiste) ---
val DarkBackground = Color(0xFF1A283A)
val BrightButtonColor = Color(0xFF00BFA5)
val DullButtonColor = Color(0xFF4A6572)
val TextColorLight = Color.White

@Composable
fun InitialScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegistration: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onGoogleLoginSuccess: () -> Unit // Lambda para cuando Google Sign-In + Firebase Auth es exitoso
) {
    val context = LocalContext.current

    // --- Configuración de Google Sign-In ---
    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Obtiene el ID de strings.xml
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    // --- Launcher para el resultado de Google Sign-In ---
    val googleAuthResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "Google Sign-In exitoso con cuenta: ${account.email}")
                firebaseAuthWithGoogle(account.idToken!!) { success -> // Pasa el idToken
                    if (success) {
                        Log.d(TAG, "Firebase Auth con Google exitoso.")
                        onGoogleLoginSuccess() // Llama a la lambda de éxito para navegar
                    } else {
                        Log.w(TAG, "Firebase Auth con Google falló.")
                        // Aquí podrías mostrar un Toast o Snackbar al usuario
                        // Ejemplo: Toast.makeText(context, "Fallo al iniciar con Google", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google Sign-In falló con ApiException: ${e.statusCode}", e)
                // Mostrar mensaje de error al usuario
            }
        } else {
            Log.w(TAG, "Google Sign-In cancelado o falló, resultCode: ${result.resultCode}")
            // Mostrar mensaje de error si es apropiado (ej: no si fue solo cancelación)
        }
    }
    // --- Fin Configuración y Launcher ---


    // Columna principal que ocupa toda la pantalla
    Column(modifier = Modifier.fillMaxSize()) {

        // --- Sección Superior (Imagen Banner) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray), // Placeholder, considera cambiar si la imagen no cubre todo
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_image_png), // Reemplaza con tu ID de recurso
                contentDescription = "Banner GGDROP Be fan. Be pro.",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // --- Sección Inferior (Botones de Acción) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f) // Ajusta el peso si es necesario con el nuevo botón
                .background(DarkBackground)
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --- Bloque Botón Iniciar Sesión (Local) ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ya soy fan/pro",
                    color = TextColorLight,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightButtonColor
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        color = TextColorLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Bloque Botón Registrarme ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Deseo ser fan/pro",
                    color = TextColorLight,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onNavigateToRegistration,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DullButtonColor
                    )
                ) {
                    Text(
                        text = "Registrarme",
                        color = TextColorLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espacio antes del botón de Google

            // --- Botón Iniciar Sesión con Google ---
            Button(
                onClick = {
                    Log.d(TAG, "Botón Google Sign-In presionado, lanzando intent.")
                    // Antes de lanzar, puedes desloguear si quieres forzar la selección de cuenta siempre (opcional)
                    // googleSignInClient.signOut().addOnCompleteListener { Log.d(TAG, "Google Sign-Out previo completado (si hubo).") }
                    googleAuthResultLauncher.launch(googleSignInClient.signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, // Fondo blanco para el botón de Google
                    contentColor = Color.Black.copy(alpha = 0.74f)    // Texto/Icono oscuro (según guías de Google)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // TODO: Reemplaza el Icon con una Image y el logo de Google desde tus drawables
                    // Ejemplo: Image(painter = painterResource(id = R.drawable.ic_google_logo), ...)
                    Icon(
                        imageVector = Icons.Filled.AccountCircle, // Icono Placeholder
                        contentDescription = "Logo Google",
                        modifier = Modifier.size(24.dp) // Ajusta tamaño según tu logo
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciar sesión con Google", fontSize = 16.sp)
                }
            }
        } // Fin Sección Inferior
    } // Fin Columna Principal
}


// --- Función auxiliar para autenticar con Firebase usando el token de Google ---
// (Esta función va FUERA del Composable InitialScreen, pero en el mismo archivo)
private fun firebaseAuthWithGoogle(idToken: String, callback: (Boolean) -> Unit) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Firebase signInWithCredential exitoso")
                // Aquí puedes obtener el usuario de Firebase: val firebaseUser = FirebaseAuth.getInstance().currentUser
                callback(true)
            } else {
                Log.w(TAG, "Firebase signInWithCredential falló", task.exception)
                callback(false)
            }
        }
}


// --- Previsualización ---
@Preview(showBackground = true, widthDp = 360, heightDp = 700) // Ajusta altura para ver el nuevo botón
@Composable
fun InitialScreenPreview_NewDesign() {
    // TuAppTheme { // Si tienes un tema

    val context = LocalContext.current
    // Para la preview, necesitas crear una instancia (puede ser una real o un mock)
    val previewAuthViewModel = AuthViewModel(UserRepository(AppDatabase.getDatabase(context).userDao()))
    InitialScreen(
        onNavigateToRegistration = {},
        onNavigateToLogin = {},
        onGoogleLoginSuccess = {}, // Añade la nueva lambda
        authViewModel = previewAuthViewModel
    )
    // }
}