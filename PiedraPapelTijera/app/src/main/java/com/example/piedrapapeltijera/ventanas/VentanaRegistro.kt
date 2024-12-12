package com.example.piedrapapeltijera.ventanas

import android.annotation.SuppressLint
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.R
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Rutas
import com.example.piedrapapeltijera.viewModels.LoginViewModel
import com.example.piedrapapeltijera.viewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentanaRegistro(navController: NavController, viewModel: LoginViewModel, mainViewModel: MainViewModel) {
    Scaffold(
        topBar = {
            var mostrarSalir by remember { mutableStateOf(false) }
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Registrar cuenta")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Rutas.login)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            Registro(navController, viewModel, mainViewModel)
        }
    }
}

@Composable
fun Registro(navController: NavController, viewModel: LoginViewModel, mainViewModel: MainViewModel) {
    val contexto = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fechaNac by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val registrado by viewModel.registrado.observeAsState(null)
    var mostrarError by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = CenterHorizontally) {
        Spacer(modifier = Modifier.height(50.dp))
        CajasDeTextoLogin(nombre, password, { nombre = it }) { password = it }
        Spacer(modifier = Modifier.height(50.dp))
        CajaDeTextoEmail(email, { email = it })
        Spacer(modifier = Modifier.height(50.dp))
        DatePickerFechaNac(fechaNac, { fechaNac = it })
        Spacer(modifier = Modifier.height(50.dp))
        BotonesRegistro({navController.navigate(Rutas.login)}){
            error = ""
            if (nombre.isEmpty() || password.isEmpty() || email.isEmpty() || fechaNac.isEmpty()) {
                error += "-Rellene todos los campos"
            }
            if (email.trim().isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                error += "\n-Email inválido"
            }

            if(fechaNac.isNotEmpty() && SimpleDateFormat("dd/MM/yyyy").parse(fechaNac).after(Date())){
                error += "\n-La fecha debe ser anterior\n\ta la fecha actual"
            }
            if (nombre.trim().contains(" ")) {
                error += "\n-No se permiten espacios\n\t en el usuario"
            }
            if (password.trim().contains(" ")) {
                error += "\n-No se permiten espacios\n\t en la contraseña"
            }
            if (error.isEmpty()) {
                viewModel.registrar(Usuario("", nombre.trim(), email.trim(), fechaNac, password.trim()))

            }else {
                mostrarError = true
            }
        }
    }
    if (mostrarError){
        MensajeError(error) { mostrarError = false }
    }
    if (registrado != null) {
        if (registrado == true) {
            Toast.makeText(contexto, "Usuario registrado", Toast.LENGTH_SHORT).show()
            mainViewModel.iniciarSesion(viewModel.usuarioLogeado.value!!)
            viewModel.restablecerRegistrado()
            navController.navigate(Rutas.partidaMaquina)
        }else{
            Toast.makeText(contexto, "El usuario ya existe", Toast.LENGTH_SHORT).show()
            viewModel.restablecerRegistrado()
        }
    }
}

@Composable
fun CajaDeTextoEmail(email: String, onEmailChange: (String) -> Unit) {
    TextField(value = email,
        onValueChange = { onEmailChange(it) },
        label = { Text("Email") })
}

@Composable
fun DatePickerFechaNac(fechaNac: String, onFechaChanged: (String) -> Unit) {
    var mostrarDialog by remember { mutableStateOf(false) }

    Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
        Text(text = "Fecha: ", fontSize = 20.sp)
        Icon(
            painter = painterResource(R.drawable.ic_calendario),
            contentDescription = "Calendario",
            modifier = Modifier.clickable { mostrarDialog = true },
            tint = colorResource(id = R.color.teal_700)
        )
        Text(text = fechaNac, fontSize = 20.sp)
    }
    if (mostrarDialog){
        DatePickerModal(onDateSelected = {onFechaChanged(it)}, onDismiss = {mostrarDialog = false})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val date = Date(datePickerState.selectedDateMillis!!)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(date)
                onDateSelected(formattedDate)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun BotonesRegistro(onCancel: () -> Unit = {}, onRegistrarse: () -> Unit = {}) {

    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
        Button(onClick = { onCancel() }) {
            Text(text = "Cancelar")
        }
        Spacer(modifier = Modifier.width(20.dp))
        Button(onClick = {onRegistrarse() }) {
            Text(text = "Registrarse")
        }
    }
}


@Composable
fun MensajeError(error:String, onCerrarMensaje: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onCerrarMensaje()  },
        title = {
            Row (verticalAlignment = Alignment.CenterVertically){
                Icon(
                    painter = painterResource(R.drawable.ic_error),
                    contentDescription = "Error",
                    tint = colorResource(id = R.color.error)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Error")
            }
        },
        text = { Text(text = error, fontSize = 16.sp) },
        confirmButton = {
            Button(
                onClick = { onCerrarMensaje() }) {
                    Text("Ok")
                }
        }
    )
}