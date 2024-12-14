package com.example.piedrapapeltijera.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Invitacion
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainViewModel: ViewModel() {
    private val _rutaActual = MutableLiveData<String>()
    val rutaActual: MutableLiveData<String> = _rutaActual

    private val _partidaEncontrada = MutableLiveData<Boolean>()
    val partidaEncontrada: LiveData<Boolean> = _partidaEncontrada

    fun restablecerPartidaEncontrada() {
        _partidaEncontrada.value = false
    }

    private val _partidaOnline = MutableLiveData<Partida?>()
    val partidaOnline: MutableLiveData<Partida?> = _partidaOnline

    fun actualizarPartidaOnline(partida: Partida) {
        if (partida.id == "") {
            val partidaSinId = hashMapOf(
                "estado" to partida.estado,
                "dificultad" to partida.dificultad,
                "user1" to partida.user1,
                "user2" to partida.user2,
                "puntos_user1" to partida.puntos_user1,
                "puntos_user2" to partida.puntos_user2,
                "estado_ronda" to partida.estado_ronda,
                "estado_user_1" to partida.estado_user_1,
                "estado_user_2" to partida.estado_user_2
            )
            db.collection(Colecciones.colPartidas)
                .add(partidaSinId)
                .addOnSuccessListener { documentReference ->
                    _partidaOnline.value = partida.copy(id = documentReference.id)
                    _partidaEncontrada.value = true
                }
        }else {
            db.collection(Colecciones.colPartidas)
                .document(partida.id)
                .get()
                .addOnSuccessListener {
                    _partidaOnline.value = it.toObject(Partida::class.java)!!.copy(id = it.id)
                    _partidaEncontrada.value = true
                }
        }
    }

    fun actualizarRutaActual(ruta: String) {
        _rutaActual.value = ruta
    }

    private val _usuarioLogeado = MutableLiveData<Usuario?>()
    val usuarioLogeado: MutableLiveData<Usuario?> = _usuarioLogeado

    fun iniciarSesion(usuario: Usuario) {
        _usuarioLogeado.value = usuario
    }

    private var db = Firebase.firestore

    private val _invitaciones = MutableLiveData<Int>()
    val invitaciones: MutableLiveData<Int> = _invitaciones

    fun buscarInvitaciones(){
        db.collection(Colecciones.colInvitaciones)
            .whereEqualTo("user_recibe.id", usuarioLogeado.value!!.id)
            .whereEqualTo("estado", 0)
            .get()
            .addOnSuccessListener { result ->

                _invitaciones.value = result.toObjects(Invitacion::class.java).size
            }

    }

}