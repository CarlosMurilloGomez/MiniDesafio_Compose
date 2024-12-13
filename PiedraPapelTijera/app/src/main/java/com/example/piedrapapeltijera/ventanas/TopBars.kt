package com.example.piedrapapeltijera.ventanas

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.R
import com.example.piedrapapeltijera.parametros.Rutas
import com.example.piedrapapeltijera.viewModels.MainViewModel
import com.example.piedrapapeltijera.viewModels.VentanaPartidaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.SwitchAccount,
                contentDescription = "Localized description",
                modifier = Modifier.size(50.dp).padding(10.dp)
            )

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
fun TopBarPartida(viewModel: VentanaPartidaViewModel, navController: NavController, onNavigationClick: () -> Unit,){
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
        navigationIcon = {
            IconButton(onClick = {onNavigationClick()}) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú hamburguesa")
            }
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
                text = { Text(text = option) },
                leadingIcon = {
                    if (option == "Perfil"){
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cuenta),
                            contentDescription = null,
                        )
                    }else if (option == "Cerrar Sesión"){
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cerrar_sesion),
                            contentDescription = null,
                        )
                    }

                }
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


@Composable
fun MenuHamburguesa(navController: NavController, mainViewModel: MainViewModel, scaffold : @Composable (CoroutineScope, DrawerState)->Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                //Cabecera
                Column(modifier = Modifier.padding(16.dp)) {
                    Image( painter = painterResource(R.drawable.logo), contentDescription = "Logo",
                        modifier = Modifier.size(100.dp))
                    Text(
                        "BIENVENIDO",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 20.sp,
                        color = Color.Blue
                    )
                }
                //A partir de aquí los elementos del menú.
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.PlayCircle,
                            contentDescription = "Jugar Offline"
                        )},
                    label = { Text(text = "Opcion Jugar Offline") },
                    selected = false,
                    onClick = {
                        mainViewModel.rutaActual.value = Rutas.partidaMaquina
                        navController.navigate(Rutas.partidaMaquina)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.TaskAlt,
                            contentDescription = "Opcion Partidas realizadas"
                        )},
                    label = { Text(text = "Partidas Realizadas") },
                    selected = false,
                    onClick = {
                        mainViewModel.rutaActual.value = Rutas.listaPartidas
                        navController.navigate(Rutas.listaPartidas)
                        scope.launch { drawerState.close() }
                    }
                )
                HorizontalDivider()
            }
        }
    ) {
        scaffold(scope, drawerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarListaPartidas(navController: NavController, onNavigationClick: () -> Unit,){
    var mostrarMenuPuntos by remember { mutableStateOf(false) }
    val opciones = listOf("Perfil", "Cerrar Sesión")

    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Lista de partidas")
        },
        navigationIcon = {
            IconButton(onClick = {onNavigationClick()}) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú hamburguesa")
            }
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
                            navController.navigate(Rutas.login)
                        }
                    }
                },
                onDismiss = { mostrarMenuPuntos = false }
            )
        }

    )
}