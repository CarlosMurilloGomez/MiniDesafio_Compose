package com.example.piedrapapeltijera.ventanas

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.piedrapapeltijera.parametros.Rutas
import com.example.piedrapapeltijera.viewModels.MainViewModel
import com.example.piedrapapeltijera.viewModels.VentanaPartidaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarLogin(){
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarRegistro(navController: NavController){
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarPartida(viewModel: VentanaPartidaViewModel, navController: NavController){
    var mostrarMenuPuntos by remember { mutableStateOf(false) }
    val opciones = listOf("Perfil", "Cerrar Sesión")

    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Partida Offline")
        },
        actions = {
            IconButton(onClick = { mostrarMenuPuntos = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Localized description"
                )
            }
            DesplegarMenuPuntos(
                expanded = mostrarMenuPuntos, opciones,
                onItemClick = { opcion ->
                    when (opcion) {
                        "Perfil" -> navController.navigate(Rutas.perfil)
                        "Cerrar Sesión" -> {
                            viewModel.cerrarPartida()
                            navController.navigate(Rutas.login)
                        }
                    }
                },
                onDismiss = { mostrarMenuPuntos = false }
            )
        }

    )
}

@Composable
fun DesplegarMenuPuntos(expanded: Boolean, opciones: List<String>, onItemClick: (String) -> Unit,  onDismiss: () -> Unit) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        opciones.forEach { option ->
            DropdownMenuItem(
                onClick = {
                    onItemClick(option)
                    onDismiss()
                },
                text = { Text(text = option) }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarPerfil(navController: NavController, mainViewModel: MainViewModel){
    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Perfil")
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigate(mainViewModel.rutaActual.value!!)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        }
    )
}