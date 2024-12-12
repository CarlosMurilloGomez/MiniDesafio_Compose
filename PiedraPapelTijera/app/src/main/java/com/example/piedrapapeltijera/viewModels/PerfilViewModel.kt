package com.example.piedrapapeltijera.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class PerfilViewModel:ViewModel() {
    private val db = Firebase.firestore

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: MutableLiveData<Usuario?> = _usuario

    fun actualizarUsuario(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun restablecerUsuario(){
        _usuario.value = null
    }

    private val _passwordChanged = MutableLiveData<Boolean>()
    val passwordChanged: MutableLiveData<Boolean> = _passwordChanged

    fun restablecerPasswordChanged() {
        _passwordChanged.value = false
    }

    private val _partidasJugadas = MutableLiveData<Int>()
    val partidasJugadas: MutableLiveData<Int> = _partidasJugadas

    private val _partidasGanadas = MutableLiveData<Int>()
    val partidasGanadas: MutableLiveData<Int> = _partidasGanadas

    private  val _usuarioBorrado = MutableLiveData<Boolean>()
    val usuarioBorrado: MutableLiveData<Boolean> = _usuarioBorrado

    fun restablecerUsuarioBorrado() {
        _usuarioBorrado.value = false
    }




    fun actualizarPassword(nuevaPassword: String) {
        val usuarioActualizado = _usuario.value?.copy(password = nuevaPassword)

        db.collection(Colecciones.colUsuarios)
            .document(_usuario.value!!.id)
            .update("password", nuevaPassword)
            .addOnSuccessListener {
                _usuario.value = usuarioActualizado
            }
            .addOnFailureListener { e ->
                Log.e("Carlos", "Error al actualizar la contraseña", e)
            }
    }

    fun sacarPartidasGanadas() {
        val partidasRef = db.collection(Colecciones.colPartidas)

        val query1 = partidasRef.whereEqualTo("user1", _usuario.value!!.id)
            .whereEqualTo("estado", 1)
            .get()

        val query2 = partidasRef.whereEqualTo("user2", _usuario.value!!.id)
            .whereEqualTo("estado", 2)
            .get()

        Tasks.whenAllSuccess<QuerySnapshot>(query1, query2)
            .addOnSuccessListener { results ->
                _partidasGanadas.value = results[0].size()+results[1].size()
            }
            .addOnFailureListener{e->
                Log.e("Carlos", "Error al sacar partidas ganadas", e)
            }
    }

    fun sacarPartidasJugadas() {
        val partidasRef = db.collection(Colecciones.colPartidas)

        val query1 = partidasRef.whereEqualTo("user1", _usuario.value!!.id)
            .whereNotEqualTo("estado", 0)
            .get()

        val query2 = partidasRef.whereEqualTo("user2", _usuario.value!!.id)
            .whereNotEqualTo("estado", 0)
            .get()

        Tasks.whenAllSuccess<QuerySnapshot>(query1, query2)
            .addOnSuccessListener { results ->
                _partidasJugadas.value = results[0].size()+results[1].size()
            }
            .addOnFailureListener{e->
                Log.e("Carlos", "Error al sacar partidas jugadas", e)
            }
    }

    fun eliminarCuenta(idUsuario: String) {
        db.collection(Colecciones.colUsuarios)
            .document(idUsuario)
            .delete()
            .addOnSuccessListener {
                _usuarioBorrado.value = true
            }

    }


}