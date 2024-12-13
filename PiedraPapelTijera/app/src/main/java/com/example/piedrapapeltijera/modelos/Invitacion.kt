package com.example.piedrapapeltijera.modelos

data class Invitacion(
    val id:String="",
    var user_envia: HashMap<String, String> = hashMapOf(),
    var user_recibe: HashMap<String, String> = hashMapOf(),
    var estado:Int=0
)
