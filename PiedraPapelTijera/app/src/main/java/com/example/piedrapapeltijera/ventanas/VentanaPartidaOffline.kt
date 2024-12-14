package com.example.piedrapapeltijera.ventanas

import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.R
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.viewModels.MainViewModel
import com.example.piedrapapeltijera.viewModels.PartidaOfflineViewModel
import kotlinx.coroutines.launch

@Composable
fun VentanaPartidaOffline(navController: NavController, viewModel: PartidaOfflineViewModel, mainViewModel: MainViewModel){
    MenuHamburguesa(navController, mainViewModel){ corrutineScope, drawerState ->
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopBarPartida ("Partida Offline", viewModel, mainViewModel, navController){
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
                PartidaOffline(viewModel, mainViewModel)
            }
        }
    }

}

@Composable
fun PartidaOffline(viewModel: PartidaOfflineViewModel, mainViewModel: MainViewModel) {
    var textoGanadorRonda by remember { mutableStateOf("") }
    val partidaPendiente by viewModel.partidaPendiente.observeAsState(null)
    var mostrarDialogoReanudar by remember { mutableStateOf(false) }
    val partida by viewModel.partida.observeAsState(null)
    var jugadaUser1 by remember { mutableStateOf(0) }
    var jugadaUser2 by remember { mutableStateOf(0) }
    val sumandoPuntos by viewModel.sumandoPuntos.observeAsState(false)
    val botonesActivados by viewModel.botonesActivados.observeAsState(true)

   if (partidaPendiente == null) {
        viewModel.buscarSiHayUnaPartidaPendiente(mainViewModel.usuarioLogeado.value!!.id)
    }

    Column (modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), horizontalAlignment = CenterHorizontally){
        if (partidaPendiente == true){
            mostrarDialogoReanudar = true
            ReanudarPartida({viewModel.reanudarPartidaPendiente()}, {viewModel.noReanudarPartidaPendiente()}){mostrarDialogoReanudar = false}
        }else {
            if (partida == null) {
                Spacer(modifier = Modifier.height(60.dp))
                NuevaPartida() { viewModel.iniciarPartida(Partida(user1 = hashMapOf("id" to mainViewModel.usuarioLogeado.value!!.id, "nombre" to mainViewModel.usuarioLogeado.value!!.nombre), user2 = hashMapOf("id" to "0","nombre" to "Máquina"), dificultad = it)) }
            } else {
                Spacer(modifier = Modifier.height(50.dp))
                Puntuacion(partida!!, partida!!.user1.get("nombre")!!, partida!!.user2.get("nombre")!!, textoGanadorRonda)
                Spacer(modifier = Modifier.height(50.dp))
                Fotos(jugadaUser1, jugadaUser2)
                Spacer(modifier = Modifier.height(50.dp))
                BotonesJugar(botonesActivados){
                    jugadaUser1 = it
                    jugadaUser2 = jugar(it, partida!!.dificultad,
                        onGanaUser = {
                            viewModel.sumarPuntoUser1()
                            textoGanadorRonda = "Ganaste esta ronda"
                        },
                        onGanaMaquina = {
                            viewModel.sumarPuntoUser2()
                            textoGanadorRonda = "Perdiste esta ronda"
                        },
                        onEmpate = {
                            textoGanadorRonda = "Empate"
                        })

                }
                if ((partida!!.puntos_user1 == 3 || partida!!.puntos_user2 == 3) && !sumandoPuntos){
                    viewModel.terminarPartida()
                    if (partida!!.puntos_user1 == 3){
                        textoGanadorRonda = "Ganador: "+partida!!.user1.get("nombre")
                    }else{
                        textoGanadorRonda = "Ganador: "+partida!!.user2.get("nombre")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        viewModel.btnTerminarPartida()
                    }) { Text(text = "Terminar partida", fontSize = 20.sp) }
                }
            }
        }
    }

}

@Composable
fun ReanudarPartida(onReanudar:() -> Unit, onNoReanudar:() -> Unit, onDismiss:() -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Partida pendiente") },
        text = { Text("¿Desea continuar la partida que tiene pendiente?") },
        confirmButton = {
            Button(onClick = {
                onReanudar()
                onDismiss()
            }) {
                Text("Reanudar")
            }
        },
        dismissButton = {
            Button(onClick = {
                onNoReanudar()
                onDismiss()
            }) {
                Text("Descartar")
            }
        })
}


@Composable
fun NuevaPartida(onIniciarPartida: (Int) -> Unit) {
    var dificultad by remember { mutableStateOf(1) }
    Text(text = "NUEVA PARTIDA", fontSize = 30.sp)
    Spacer(modifier = Modifier.height(100.dp))
    Dificultad(dificultad){dificultad = it}
    Spacer(modifier = Modifier.height(60.dp))
    Button(onClick = { onIniciarPartida(dificultad) }){Text(text = "Iniciar partida")}
}

@Composable
fun Dificultad(dificultad: Int, onDificultadChange: (Int) -> Unit = {}) {
    Column (modifier = Modifier.width(250.dp), verticalArrangement = Arrangement.Center) {
        Text(text = "Dificultad: ", fontSize = 20.sp)
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Slider(
                value = dificultad.toFloat(),
                valueRange = 1f..3f,
                steps = 1,
                onValueChange = {
                    onDificultadChange(it.toInt())
                }
            )
            when (dificultad){
                1 -> Text(text = "Fácil")
                2 -> Text(text = "Normal")
                3 -> Text(text = "Difícil")
            }

        }
    }
}

@Composable
fun Puntuacion(partida: Partida, user1: String, user2:String, textoGanadorRonda: String){
    Text(text = user1+": "+partida.puntos_user1.toString()+" - "+user2+": "+partida.puntos_user2.toString(),
        fontSize = 30.sp)
    Spacer(modifier = Modifier.height(10.dp))
    Text(text = textoGanadorRonda, fontSize = 20.sp)
}

@Composable
fun Fotos(jugadaUser1: Int, jugadaUser2: Int){
    var sizeFotos = 100.dp
    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
        Column (){
            when(jugadaUser1){
                1 -> Image(painter = painterResource(id = R.drawable.piedra),
                    contentDescription = "JugadaUser1", modifier = Modifier.padding(10.dp).height(sizeFotos).width(sizeFotos))
                2 -> Image(painter = painterResource(id = R.drawable.papel),
                    contentDescription = "JugadaUser1", modifier = Modifier.padding(10.dp).height(sizeFotos).width(sizeFotos))
                3 -> Image(painter = painterResource(id = R.drawable.tijeras),
                    contentDescription = "JugadaUser1", modifier = Modifier.padding(10.dp).height(sizeFotos).width(sizeFotos))
                else -> Image(painter = painterResource(id = R.drawable.piedra_papel_tijera),
                    contentDescription = "JugadaUser1", modifier = Modifier.padding(10.dp).height(sizeFotos).width(sizeFotos))
            }
        }
        Column (){
            Text(text = "VS", fontSize = 20.sp)
        }
        Column (){
            when(jugadaUser2){
                1 -> Image(painter = painterResource(id = R.drawable.piedra),
                    contentDescription = "JugadaUser2", modifier = Modifier.padding(10.dp).height(sizeFotos).width(sizeFotos))
                2 -> Image(painter = painterResource(id = R.drawable.papel),
                    contentDescription = "JugadaUser2", modifier = Modifier.padding(10.dp).height(sizeFotos).width(sizeFotos))
                3 -> Image(painter = painterResource(id = R.drawable.tijeras),
                    contentDescription = "JugadaUser2", modifier = Modifier.padding(10.dp).height(sizeFotos).width(sizeFotos))
                else -> Image(painter = painterResource(id = R.drawable.piedra_papel_tijera),
                    contentDescription = "JugadaUser2", modifier = Modifier.padding(10.dp).height(sizeFotos).width(sizeFotos))
            }
        }
    }

}

@Composable
fun BotonesJugar(botonesActivados: Boolean, onJugar: (Int) -> Unit){
    Row {
        Button(onClick = { onJugar(1) }, enabled = botonesActivados) {
            Text(text = "Piedra", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(30.dp))
        Button(onClick = { onJugar(2) }, enabled = botonesActivados) {
            Text(text = "Papel", fontSize = 20.sp)
        }

    }
    Spacer(modifier = Modifier.height(25.dp))
    Button(onClick = { onJugar(3) }, enabled = botonesActivados) {
        Text(text = "Tijeras", fontSize = 20.sp)
    }
}

fun jugar(jugadaUser1: Int, dificultad: Int, onGanaUser : () -> Unit, onGanaMaquina : () -> Unit, onEmpate : () -> Unit): Int {
    var jugadaUser2 = 0
    var contrarioMalo = 0
    var contrarioBueno = 0
    var posibilidades = 0
    when (jugadaUser1){
        1 -> {contrarioMalo = 2
            contrarioBueno = 3}
        2 -> {contrarioMalo = 3
            contrarioBueno = 1}
        3 -> {contrarioMalo = 1
            contrarioBueno = 2}
    }
    when (dificultad){
        1 -> {
            posibilidades = (1..100).random()
            when (posibilidades){
                in 1..50 -> jugadaUser2 = contrarioBueno
                in 51..75 -> jugadaUser2 = contrarioMalo
                else -> jugadaUser2 = jugadaUser1
            }
        }
        2 -> {
            jugadaUser2 = (1..3).random()
        }
        3 -> {
            posibilidades = (1..100).random()
            when (posibilidades){
                in 1..50 -> jugadaUser2 = contrarioMalo
                in 51..75 -> jugadaUser2 = contrarioBueno
                else -> jugadaUser2 = jugadaUser1
            }
        }
    }
    when (jugadaUser2){
        contrarioBueno -> onGanaUser()
        contrarioMalo -> onGanaMaquina()
        else -> { onEmpate()}
    }
    return jugadaUser2
}







