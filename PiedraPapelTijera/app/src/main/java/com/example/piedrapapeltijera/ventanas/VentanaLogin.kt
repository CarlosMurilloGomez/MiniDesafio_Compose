package com.example.piedrapapeltijera.ventanas

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.parametros.Rutas
import com.example.piedrapapeltijera.viewModels.LoginViewModel
import com.example.piedrapapeltijera.viewModels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentanaLogin(navController: NavController, viewModel: LoginViewModel, mainViewModel: MainViewModel) {

    Scaffold(
        topBar = {
            var mostrarSalir by remember { mutableStateOf(false) }
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Iniciar Sesion")
                },
                actions = {
                    if (mostrarSalir){
                        MensajeSalir { mostrarSalir = false }
                    }
                    IconButton(
                        onClick = {
                            mostrarSalir = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Login(navController, viewModel, mainViewModel)
        }
    }
}


@Composable
fun Login(navController: NavController, viewModel: LoginViewModel, mainViewModel: MainViewModel) {
    val contexto = LocalContext.current
    var usuario by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    val login by viewModel.login.observeAsState(null)
    val usuarioLogeado by viewModel.usuarioLogeado.observeAsState(null)

    Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = CenterHorizontally){
        Spacer(modifier = Modifier.height(150.dp))

        CajasDeTextoLogin(usuario, contraseña, {usuario = it}){contraseña = it}
        Spacer(modifier = Modifier.height(70.dp))

        BotonesLogin({
            if (usuario.isEmpty() || contraseña.isEmpty()){
                Toast.makeText(contexto, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.iniciarSesion(usuario.trim(), contraseña.trim())

            }
        }){
            navController.navigate(Rutas.registro)
        }
        if (login != null) {
            if (login==true){
                Toast.makeText(contexto, "Sesion iniciada", Toast.LENGTH_SHORT).show()
                mainViewModel.iniciarSesion(usuarioLogeado!!)
                viewModel.restablecerLogin()
                navController.navigate(Rutas.partidaMaquina)
            }else{
                Toast.makeText(contexto, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                viewModel.restablecerLogin()
            }

        }
    }
}




@Composable
fun CajasDeTextoLogin(usuario: String, contraseña: String, onUsuarioChange: (String) -> Unit, onContraseñaChange: (String) -> Unit) {

    Column (verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally){
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            TextField(value = usuario, onValueChange = { onUsuarioChange(it) }, label = { Text("Usuario") })
        }
        Spacer(modifier = Modifier.height(50.dp))

        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
            var showPassword by remember { mutableStateOf(value = false) }
            TextField(value = contraseña, onValueChange = { onContraseñaChange(it) },
                label = { Text("Contraseña")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    if (showPassword) {
                        IconButton(onClick = { showPassword = false }) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = "hide_password"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { showPassword = true }) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = "hide_password"
                            )
                        }
                    }
                },
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )
        }

    }
}

@Composable
fun BotonesLogin(onIniciarSesion: () -> Unit, onRegistrarse: () -> Unit) {
    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
        Button(onClick = { onIniciarSesion() }) {
            Text(text = "Iniciar Sesión")
        }
        Spacer(modifier = Modifier.width(20.dp))
        Button(onClick = { onRegistrarse() }) {
            Text(text = "Registrarse")
        }
    }
}



@Composable
fun MensajeSalir(onCerrarMensaje: () -> Unit) {
    val activity = LocalContext.current as Activity

    AlertDialog(
        onDismissRequest = {
            onCerrarMensaje()
        },
        title = {
            Text(text = "Salir")
        },
        text = {
            Text("¿Seguro que deseas salir de la aplicación?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onCerrarMensaje()
                    activity.finish()
                }) {
                Text("Sí")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onCerrarMensaje()
                }) {
                Text("No")
            }
        }
    )
}