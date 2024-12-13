package com.example.piedrapapeltijera.ventanas

import android.app.Activity
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Rutas
import com.example.piedrapapeltijera.viewModels.LoginViewModel
import com.example.piedrapapeltijera.viewModels.MainViewModel
import com.example.piedrapapeltijera.viewModels.PerfilViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun VentanaPerfil(navController: NavController,  viewModel: PerfilViewModel, mainViewModel: MainViewModel){
    Scaffold(
        topBar = {
            TopBarPerfil(navController, mainViewModel)
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Perfil(navController, viewModel, mainViewModel)
        }
    }
}

@Composable
fun Perfil(navController: NavController, viewModel: PerfilViewModel, mainViewModel: MainViewModel) {
    val usuario by mainViewModel.usuarioLogeado.observeAsState(null)
    val passwordChanged by viewModel.passwordChanged.observeAsState(false)

    if (usuario != null) {
        viewModel.actualizarUsuario(usuario!!)
        var contraseña by remember { mutableStateOf(usuario!!.password) }
        val usuarioBorrado by viewModel.usuarioBorrado.observeAsState(false)

        Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = CenterHorizontally){
            Spacer(modifier = Modifier.padding(20.dp))
            InfoUsuario(usuario!!)
            Spacer(modifier = Modifier.padding(20.dp))
            EditarPassword(contraseña, {contraseña = it},{viewModel.actualizarPassword(contraseña)}){contraseña = usuario!!.password}
            Spacer(modifier = Modifier.padding(20.dp))
            Estadisticas(viewModel)
            Spacer(modifier = Modifier.padding(20.dp))
            BotonEliminarCuenta(){viewModel.eliminarCuenta(usuario!!.id)}
        }
        if (passwordChanged) {
            mainViewModel.iniciarSesion(viewModel.usuario.value!!)
            viewModel.restablecerPasswordChanged()
        }
        if (usuarioBorrado) {
            viewModel.restablecerUsuarioBorrado()
            viewModel.restablecerUsuario()
            navController.navigate(Rutas.login)
        }
    }
}

@Composable
fun InfoUsuario(usuario: Usuario) {
    Column (modifier = Modifier.padding(horizontal = 20.dp)){
        Row (){
            Text(text = "Nombre: ", fontWeight = FontWeight.Bold)
            Text(text = usuario.nombre)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row (){
            Text(text = "Email: ", fontWeight = FontWeight.Bold)
            Text(text = usuario.email)
        }
        Spacer(modifier = Modifier.height(10.dp))

        val fechaNac by remember { mutableStateOf( LocalDate.parse(usuario.fechaNac, DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))) }
        val fechaActual by remember { mutableStateOf(LocalDate.now()) }
        val edad by remember { mutableStateOf(Period.between(fechaNac, fechaActual).years.toString()) }

        Row (){
            Text(text = "Edad: ", fontWeight = FontWeight.Bold)
            Text(text = edad+" años")
        }
    }
}

@Composable
fun EditarPassword(contraseña: String, onChangePassword: (String) -> Unit, onActualizarPassword: () -> Unit, onCancelar: () -> Unit) {
    var mostrarBotonEditar by remember { mutableStateOf(true) }
    var mostrarBotonActualizar by remember { mutableStateOf(false) }
    Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = CenterHorizontally){
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
            var showPassword by remember { mutableStateOf(value = false) }
            TextField(value = contraseña, onValueChange = { onChangePassword(it) },
                enabled = mostrarBotonActualizar,
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
        if (mostrarBotonEditar){
            Button(onClick = { mostrarBotonEditar = false; mostrarBotonActualizar = true }) {
                Text(text = "Editar contraseña")
            }
        }
        if (mostrarBotonActualizar){
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Button(onClick = { onActualizarPassword(); mostrarBotonEditar = true; mostrarBotonActualizar = false }) {
                    Text(text = "Actualizar")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = { mostrarBotonEditar = true; mostrarBotonActualizar = false ; onCancelar()}) {
                    Text(text = "Cancelar")
                }
            }

        }


    }
}

@Composable
fun Estadisticas(viewModel: PerfilViewModel) {
    var buscarPartidas by remember { mutableStateOf(false) }
    val partidasJugadas by viewModel.partidasJugadas.observeAsState(null)
    val partidasGanadas by viewModel.partidasGanadas.observeAsState(null)
    if (!buscarPartidas){
        viewModel.sacarPartidasJugadas()
        viewModel.sacarPartidasGanadas()
        buscarPartidas = true
    }

    if (partidasJugadas != null && partidasGanadas != null){
        Column (modifier = Modifier.padding(horizontal = 20.dp)){
            Text(text = "Estadísticas: ", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Partidas jugadas: "+partidasJugadas)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Partidas ganadas: "+partidasGanadas)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Partidas perdidas: "+(partidasJugadas!!-partidasGanadas!!))
        }
    }


}

@Composable
fun BotonEliminarCuenta(onEliminarCuenta: () -> Unit) {
    var mostrarBorrarCuenta by remember { mutableStateOf(false) }
    if (mostrarBorrarCuenta){
        MensajeBorrarCuenta({ mostrarBorrarCuenta = false }){onEliminarCuenta()}
    }
    Button(onClick = { mostrarBorrarCuenta = true }) {
        Text(text = "Eliminar cuenta")
    }
}


@Composable
fun MensajeBorrarCuenta(onCerrarMensaje: () -> Unit, onEliminarCuenta: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onCerrarMensaje()
        },
        title = {
            Text(text = "Borrar")
        },
        text = {
            Text("¿Seguro que deseas eliminar tu cuenta? (acción irreversible)")
        },
        confirmButton = {
            Button(
                onClick = {
                    onCerrarMensaje()
                    onEliminarCuenta()
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
