package com.example.piedrapapeltijera.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Invitacion
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainViewModel: ViewModel() {
    private val _rutaActual = MutableLiveData<String>()
    val rutaActual: MutableLiveData<String> = _rutaActual

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