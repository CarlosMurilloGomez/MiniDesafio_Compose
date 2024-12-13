package com.example.piedrapapeltijera.ventanas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.R
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.viewModels.ListaPartidasViewModel
import com.example.piedrapapeltijera.viewModels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun VentanaListaPartidas(navController: NavController, viewModel: ListaPartidasViewModel, mainViewModel: MainViewModel) {
    MenuHamburguesa(navController, mainViewModel){ corrutineScope, drawerState ->
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopBarListaPartidas (navController){
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
                ListaPartidas(viewModel, mainViewModel)
            }
        }
    }
}

@Composable
fun ListaPartidas(viewModel: ListaPartidasViewModel, mainViewModel: MainViewModel) {
    val partidas by viewModel.partidas.observeAsState(emptyList())
    var cargaInicial by remember { mutableStateOf(false) }
    if (!cargaInicial){
        viewModel.cargarPartidas(mainViewModel.usuarioLogeado.value!!.id)
        cargaInicial = true
    }

    if (partidas.isEmpty()){
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
            Text(text = "AUN NO HAS JUGADO NINGUNA PARTIDA", fontSize = 50.sp,
                textAlign = TextAlign.Center, modifier = Modifier.padding(20.dp), lineHeight = 50.sp
            )
        }
    }else{
        when (opcionesLista()){
            1 -> viewModel.cargarPartidas(mainViewModel.usuarioLogeado.value!!.id)
            2 -> viewModel.cargarGanadas(mainViewModel.usuarioLogeado.value!!.id)
            3 -> viewModel.cargarPerdidas(mainViewModel.usuarioLogeado.value!!.id)
        }
        Lista(partidas, mainViewModel.usuarioLogeado.value!!.id)
    }


}


@Composable
fun opcionesLista():Int {
    var selected by remember { mutableStateOf(1) }
    Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
            RadioButton(selected = selected == 1, onClick = { selected = 1 })
            Text(text = "Todas", modifier = Modifier.clickable { selected = 1 })
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
            RadioButton(selected = selected == 2, onClick = {  selected = 2 })
            Text(text = "Ganadas", modifier = Modifier.clickable { selected = 2 })
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
            RadioButton(selected = selected == 3, onClick = { selected = 3 })
            Text(text = "Perdidas", modifier = Modifier.clickable { selected = 3 })
        }
    }
    return selected
}



@Composable
fun Lista(partidas: List<Partida>, idUsuario: String) {
    LazyColumn {
        items(partidas) { partida ->
            PartidaItem(partida, idUsuario)
        }
    }
}

@Composable
fun PartidaItem(partida: Partida, idUsuario: String) {
    var colorBorde by remember { mutableStateOf(Color.Blue) }
    var colorFondo by remember { mutableStateOf(R.color.white) }
    if (partida.user1.get("id") == idUsuario && partida.puntos_user1 == 3){
        colorBorde = Color.Green
        colorFondo = R.color.partidaGanada
    }
    else if (partida.user2.get("id") == idUsuario && partida.puntos_user2 == 3){
        colorBorde = Color.Green
        colorFondo = R.color.partidaGanada
    }
    else{
        colorBorde = Color.Red
        colorFondo = R.color.partidaPerdida
    }

    Card(border = BorderStroke(2.dp, colorBorde),
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 5.dp, start = 1.dp, end = 1.dp),
        colors = cardColors(
            containerColor = colorResource(colorFondo),
            contentColor = Color.Black
        )
    ){
        Text(text = partida.user1.get("nombre") +" VS "+partida.user2.get("nombre"), fontSize = 20.sp,
            modifier = Modifier.align(CenterHorizontally).padding(top= 10.dp, bottom = 5.dp), fontWeight = FontWeight.Bold)
        Text(text = partida.toString(), fontSize = 20.sp,
            modifier = Modifier.align(CenterHorizontally).padding(10.dp))
    }
}