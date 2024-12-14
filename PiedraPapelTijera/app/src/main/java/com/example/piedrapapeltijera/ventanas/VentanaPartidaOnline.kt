package com.example.piedrapapeltijera.ventanas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.parametros.Rutas
import com.example.piedrapapeltijera.viewModels.MainViewModel
import com.example.piedrapapeltijera.viewModels.PartidaOfflineViewModel
import kotlinx.coroutines.launch

@Composable
fun VentanaPartidaOnline(navController: NavController, viewModel: PartidaOfflineViewModel, mainViewModel: MainViewModel) {
    MenuHamburguesa(navController, mainViewModel){ corrutineScope, drawerState ->
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopBarPartida ("Partida Online", viewModel, mainViewModel, navController){
                    corrutineScope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }

            }) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PartidaOnline(viewModel, mainViewModel)
            }
        }
    }
}

@Composable
fun PartidaOnline(viewModel: PartidaOfflineViewModel, mainViewModel: MainViewModel) {
    var revanchaPedida by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    var idUsuario = mainViewModel.usuarioLogeado.value!!.id
    var textoGanadorRonda by remember { mutableStateOf("") }
    val partida by viewModel.partida.observeAsState(null)
    var jugadaUser1 by remember { mutableStateOf(0) }
    var jugadaUser2 by remember { mutableStateOf(0) }
    val sumandoPuntos by viewModel.sumandoPuntos.observeAsState(false)
    var partidaCargada by remember { mutableStateOf(false) }
    val botonActivado by viewModel.botonesActivados.observeAsState(true)
    var partidaTerminada by remember { mutableStateOf(false) }
    if (!partidaCargada){
        if (mainViewModel.partidaOnline.value != null) {
            viewModel.cargarPartida(mainViewModel.partidaOnline.value!!.id)
            partidaCargada = true
            viewModel.activarBotones()
        }
    }
    if (partida != null) {
        if (partida!!.user1.get("id") == idUsuario) {
            textoGanadorRonda = generarTextoRonda(jugadaUser1, jugadaUser2, 1)
        }
        else{
            textoGanadorRonda = generarTextoRonda(jugadaUser1, jugadaUser2, 2)
        }
        Column (modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), horizontalAlignment = CenterHorizontally){
            Button(onClick = {
                viewModel.cargarPartida(partida!!.id)
            }) { Text(text = "Refrescar partida", fontSize = 20.sp) }
            Spacer(modifier = Modifier.height(50.dp))
            Puntuacion(partida!!, partida!!.user1.get("nombre")!!, partida!!.user2.get("nombre")!!, textoGanadorRonda)
            Spacer(modifier = Modifier.height(50.dp))
            Fotos(jugadaUser1, jugadaUser2)
            Spacer(modifier = Modifier.height(50.dp))

            if ((partida!!.puntos_user1 == 3 || partida!!.puntos_user2 == 3) && !sumandoPuntos){
                if (!partidaTerminada){
                    viewModel.terminarPartida()
                    partidaTerminada = true
                }
                if (partida!!.puntos_user1 == 3){
                    textoGanadorRonda = "Ganador: "+partida!!.user1.get("nombre")
                }else{
                    textoGanadorRonda = "Ganador: "+partida!!.user2.get("nombre")
                }
                Button(enabled= !revanchaPedida, onClick = {
                    revanchaPedida = true
                    viewModel.revancha(mainViewModel.usuarioLogeado.value!!.id, mainViewModel.usuarioLogeado.value!!.nombre, contexto)
                }) { Text(text = "Revancha", fontSize = 20.sp) }
            }else if (partida!!.estado_user_1 !=0 && partida!!.estado_user_2 !=0) {
                jugadaUser1 = partida!!.estado_user_1
                jugadaUser2 = partida!!.estado_user_2
                if (partida!!.user1.get("id") == idUsuario) {
                    textoGanadorRonda = generarTextoRonda(jugadaUser1, jugadaUser2, 1)
                }
                else{
                    textoGanadorRonda = generarTextoRonda(jugadaUser1, jugadaUser2, 2)
                }
                if (partida!!.estado_ronda == 0 ||
                    (partida!!.estado_ronda == 1 && partida!!.user1.get("id") != idUsuario) ||
                    (partida!!.estado_ronda == 2 && partida!!.user2.get("id") != idUsuario)
                ) {
                    BotonListo(){ viewModel.btnListo(idUsuario) }
                }else if((partida!!.estado_ronda == 1 && partida!!.user1.get("id") == idUsuario) ||
                    (partida!!.estado_ronda == 2 && partida!!.user2.get("id") == idUsuario)) {
                    Text("Esperando al otro jugador...")
                }
            }else if ((partida!!.estado_user_1 != 0 && partida!!.user1.get("id") == idUsuario)||
                (partida!!.estado_user_2 != 0 && partida!!.user2.get("id") == idUsuario)){
                Text("Esperando al otro jugador...")
            }else {
                BotonesJugar(botonActivado){ jugada ->
                    if (partida!!.user1.get("id") == idUsuario){
                        viewModel.jugar(1, jugada)
                    }else{
                        viewModel.jugar(2, jugada)
                    }
                }
            }



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

fun generarTextoRonda(jugadaUser1: Int, jugadaUser2: Int, user: Int): String {
    when (jugadaUser1){
        1 -> {
            when (jugadaUser2){
                1 -> return "Empate"
                2 -> return  if (user == 1) "Perdiste esta ronda" else "Ganaste esta ronda"
                3 -> return if (user == 1) "Ganaste esta ronda" else "Perdiste esta ronda"
                else -> return "Empate"
            }
        }
        2 -> {
            when (jugadaUser2){
                1 -> return if (user == 1) "Ganaste esta ronda" else "Perdiste esta ronda"
                2 -> return "Empate"
                3 -> return if (user == 1) "Perdiste esta ronda" else "Ganaste esta ronda"
                else -> return "Empate"
            }
        }
        3 -> {
            when (jugadaUser2){
                1 -> return if (user == 1) "Perdiste esta ronda" else "Ganaste esta ronda"
                2 -> return if (user == 1) "Ganaste esta ronda" else "Perdiste esta ronda"
                3 -> return "Empate"
                else -> return "Empate"
            }
        }
        else -> return "Empate"

    }
}

@Composable
fun BotonListo(onClick: () -> Unit){
    Button(onClick = { onClick() }) {
        Text(text = "Listo", fontSize = 20.sp)
    }
}