package com.example.piedrapapeltijera.modelos

data class Partida(
    var id: String="",
    var estado: Int=0,
    var dificultad: Int=0,
    var user1: String="",
    var user2:String="",
    var puntos_user1: Int=0,
    var puntos_user2: Int=0,
    var estado_user_1:Int=0,
    var estado_user_2:Int=0,
    var idGanador:String=""
)
