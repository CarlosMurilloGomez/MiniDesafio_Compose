package com.example.piedrapapeltijera.ventanas

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piedrapapeltijera.R
import com.example.piedrapapeltijera.modelos.Invitacion
import com.example.piedrapapeltijera.viewModels.InvitacionesViewModel
import com.example.piedrapapeltijera.viewModels.MainViewModel

@Composable
fun VentanaInvitaciones(navController: NavController,viewModel: InvitacionesViewModel,  mainViewModel: MainViewModel){
    Scaffold(
        topBar = {
            TopBarInvitaciones(navController, mainViewModel)
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BodyInvitaciones(navController, viewModel, mainViewModel)
        }
    }

}
@Composable
fun BodyInvitaciones(navController: NavController, viewModel: InvitacionesViewModel, mainViewModel: MainViewModel){
    val contexto = LocalContext.current
    val invitaciones by viewModel.invitaciones.observeAsState(null)
    viewModel.buscarInvitaciones(mainViewModel.usuarioLogeado.value!!.id)

    if (invitaciones != null){
        if (invitaciones!!.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
                Text(text = "NO TIENES NOTIFICACIONES", fontSize = 40.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(20.dp), lineHeight = 50.sp
                )
            }
        } else {
            ListaInvitaciones(invitaciones!!, {
                //AQUI SE VA A LA PARTIDA CON EL OTRO JUGADOR
            }){
                viewModel.borrarInvitacion(it)
                Toast.makeText(contexto, "Invitación rechazada", Toast.LENGTH_SHORT).show()

            }
        }
    }


}

@Composable
fun ListaInvitaciones(invitaciones: List<Invitacion>, onAceptar: (Invitacion) -> Unit, onRechazar: (Invitacion) ->Unit) {
    LazyColumn {
        items(invitaciones) { invitacion ->
            ItemInvitacion(invitacion, {onAceptar(invitacion)}) {onRechazar(invitacion)}
        }
    }
}

@Composable
fun ItemInvitacion(invitacion: Invitacion, onAceptar: (Invitacion) -> Unit, onRechazar: (Invitacion) ->Unit){
    Card(border = BorderStroke(2.dp, Color.Black), modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 5.dp, start = 1.dp, end = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.notificacion_fondo),
            contentColor = Color.Black
        )) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Invitación a partida de: " + invitacion.user_envia.get("nombre")!!, fontWeight = FontWeight.Bold)

            IconButton(onClick = { onAceptar(invitacion) }) {
                Icon(
                    painterResource(R.drawable.ic_aceptar2),
                    contentDescription = "Aceptar",
                    tint = Color.Green
                )
            }
            IconButton(onClick = { onRechazar(invitacion) }) {
                Icon(
                    painterResource(R.drawable.ic_rechazar2),
                    contentDescription = "Rechazar",
                    tint = Color.Red
                )
            }
        }
    }
}
