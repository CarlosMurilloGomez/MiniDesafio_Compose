package com.example.piedrapapeltijera.ventanas

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.R
import com.example.piedrapapeltijera.modelos.Invitacion
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Rutas
import com.example.piedrapapeltijera.viewModels.ListaUsuariosViewModel
import com.example.piedrapapeltijera.viewModels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun VentanaListaUsuarios(navController: NavController, viewModel: ListaUsuariosViewModel, mainViewModel: MainViewModel) {
    MenuHamburguesa(navController, mainViewModel){ corrutineScope, drawerState ->
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopBarListaPartidas (" Otros jugadores", navController, mainViewModel){
                    corrutineScope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }

            }) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                BodyUsuarios(navController, viewModel, mainViewModel)
            }
        }
    }
}

@Composable
fun BodyUsuarios(navController: NavController, viewModel: ListaUsuariosViewModel, mainViewModel: MainViewModel){
    val contexto = LocalContext.current
    val partidaEncontrada by mainViewModel.partidaEncontrada.observeAsState(Partida())
    val usuarios by viewModel.usuarios.observeAsState(null)
    val partidasPendientes by viewModel.partidasPendientes.observeAsState(emptyList())
    val invitacionesPendientes by viewModel.invitacionesPendientes.observeAsState(emptyList())

    viewModel.cargarUsuarios(mainViewModel.usuarioLogeado.value!!.id)
    viewModel.cargarPartidasPendientes(mainViewModel.usuarioLogeado.value!!.id)
    viewModel.cargarInvitacionesPendientes(mainViewModel.usuarioLogeado.value!!.id)

    if (usuarios != null){
        if (usuarios!!.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
                Text(text = "AUN NO HAY OTROS JUGADORES :(", fontSize = 50.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(20.dp), lineHeight = 50.sp
                )
            }
        } else {
            Spacer(modifier = Modifier.height(5.dp))
            ListaUsuarios(navController, usuarios!! ,partidasPendientes, invitacionesPendientes, {
                viewModel.enviarInvitacion(Invitacion("", hashMapOf("id" to mainViewModel.usuarioLogeado.value!!.id, "nombre" to mainViewModel.usuarioLogeado.value!!.nombre) , hashMapOf("id" to it.id, "nombre" to it.nombre), 0))
                Toast.makeText(contexto, "Invitación de partida enviada", Toast.LENGTH_SHORT).show()
            }){
                mainViewModel.actualizarPartidaOnline(it)
            }
        }
        if (partidaEncontrada == true){
            mainViewModel.restablecerPartidaEncontrada()
            navController.navigate(Rutas.partidaOnline)
        }
    }
    else{
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
            Text(text = "CARGANDO...", fontSize = 30.sp,
                textAlign = TextAlign.Center, modifier = Modifier.padding(20.dp), lineHeight = 50.sp
            )
        }
    }

}

@Composable
fun ListaUsuarios(navController: NavController, usuarios: List<Usuario>, partidasPendientes: List<Partida>, invitacionesPendientes: List<Invitacion>, onInvitar: (Usuario) -> Unit, onJugar: (Partida) ->Unit) {
    LazyColumn {
        items(usuarios) { usuario ->
            ItemUsuario(navController, usuario, partidasPendientes, invitacionesPendientes, {onInvitar(usuario)}) {onJugar(it)}
        }
    }
}

@Composable
fun ItemUsuario(navController: NavController, usuario: Usuario, partidasPendientes: List<Partida>, invitacionesPendientes: List<Invitacion>, onInvitar: (Usuario) -> Unit, onJugar: (Partida) ->Unit){
    Card(border = BorderStroke(2.dp, Color.Black), modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 5.dp, start = 1.dp, end = 1.dp),
        colors = cardColors(
            containerColor = colorResource(R.color.usuario),
            contentColor = Color.Black
        )){
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            InfoUsuario(usuario)
            Spacer(modifier = Modifier.width(10.dp))
            //si ya tiene una partida iniciada con el usuario logeado
            var partida:Partida? by remember { mutableStateOf(null) }
            partida = partidasPendientes.find {(it.user1.get("id") == usuario.id || it.user2.get("id") == usuario.id)}
            if (partida != null){
                IconButton(onClick = {
                    onJugar(partida!!)
                    //navController.navigate(Rutas.partidaOnline)
                                     }, modifier = Modifier.padding(end = 25.dp)) {
                    Icon(
                        painterResource(R.drawable.ic_jugar),
                        contentDescription = "Jugar",
                        tint = Color.Green,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }
            //si no tiene invitaciones pendientes con el usuario logeado
            else if (invitacionesPendientes.find {(it.user_recibe.get("id") == usuario.id || it.user_envia.get("id") == usuario.id)} == null) {
                IconButton(onClick = { onInvitar(usuario) }, modifier = Modifier.padding(end = 25.dp)) {
                    Icon(
                        painterResource(R.drawable.ic_invitar),
                        contentDescription = "Invitar",
                        tint = Color.Blue,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

        }
    }
}