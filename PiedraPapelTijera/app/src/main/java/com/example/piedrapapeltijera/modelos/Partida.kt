package com.example.piedrapapeltijera.modelos

data class Partida(
    var id: String="",
    var estado: Int=0,
    var dificultad: Int=0,
    var user1: HashMap<String, String> = hashMapOf(),
    var user2:HashMap<String, String> = hashMapOf(),
    var puntos_user1: Int=0,
    var puntos_user2: Int=0,
    var estado_user_1:Int=0,
    var estado_user_2:Int=0,
    var fecha_hora:HashMap<String, String> = hashMapOf()
){
    override fun toString(): String {
        var ganador = when (estado) {
            1 -> user1.get("nombre")
            2 -> user2.get("nombre")
            else -> "pendiente"
        }
        var dificultad = when (dificultad) {
            1 -> "Fácil"
            2 -> "Normal"
            3 -> "Difícil"
            else -> ""
        }

        return "-Ganador: $ganador\n"+
               "-Marcador: $puntos_user1 - $puntos_user2\n" +
               "-Dificultad: $dificultad\n"+
               "-Terminada el ${fecha_hora.get("fecha")}\n   a las ${fecha_hora.get("hora")}"
    }
}
