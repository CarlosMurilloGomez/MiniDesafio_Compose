package com.example.piedrapapeltijera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Rutas
import com.example.piedrapapeltijera.ui.theme.PiedraPapelTijeraTheme
import com.example.piedrapapeltijera.ventanas.VentanaLogin
import com.example.piedrapapeltijera.ventanas.VentanaPartidaMaquina
import com.example.piedrapapeltijera.ventanas.VentanaPerfil
import com.example.piedrapapeltijera.ventanas.VentanaRegistro
import com.example.piedrapapeltijera.viewModels.LoginViewModel
import com.example.piedrapapeltijera.viewModels.MainViewModel
import com.example.piedrapapeltijera.viewModels.PerfilViewModel
import com.example.piedrapapeltijera.viewModels.VentanaPartidaViewModel

class MainActivity : ComponentActivity() {
    private val loginViewModel = LoginViewModel()
    private val partidaViewModel =  VentanaPartidaViewModel()
    private val mainViewModel = MainViewModel()
    private val perfilViewModel = PerfilViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PiedraPapelTijeraTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Rutas.login){
                    composable(Rutas.login){
                        VentanaLogin(navController, loginViewModel, mainViewModel)
                    }
                    composable(Rutas.registro){
                        VentanaRegistro(navController, loginViewModel, mainViewModel)
                    }
                    composable(Rutas.partidaMaquina){
                        VentanaPartidaMaquina(navController, partidaViewModel, mainViewModel)
                    }
                    composable(Rutas.perfil){
                        VentanaPerfil(navController, perfilViewModel, mainViewModel)
                    }
                }
            }
        }
    }
}

