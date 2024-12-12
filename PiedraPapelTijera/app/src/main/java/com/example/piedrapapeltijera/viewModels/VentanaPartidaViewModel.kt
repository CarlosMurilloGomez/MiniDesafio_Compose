package com.example.piedrapapeltijera.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class VentanaPartidaViewModel {
    val db = Firebase.firestore

    private val _partidaPendiente = MutableLiveData<Boolean?>()
    val partidaPendiente: LiveData<Boolean?> = _partidaPendiente

    fun reanudarPartidaPendiente(){
        _partidaPendiente.value = false
    }

    fun noReanudarPartidaPendiente(){
        _partidaPendiente.value = false

        db.collection(Colecciones.colPartidas).document(partida.value!!.id)
            .delete()
            .addOnSuccessListener { Log.e("Carlos", "Documento borrado.!") }
            .addOnFailureListener { e -> Log.w("Carlos", "Error al borrar el documento.", e) }
        _partida.value = null
    }

    private val _partida = MutableLiveData<Partida?>()
    val partida: LiveData<Partida?> = _partida

    private val _user1 = MutableLiveData<Usuario?>()
    val user1: LiveData<Usuario?> = _user1

    private val _user2 = MutableLiveData<Usuario?>()
    val user2: LiveData<Usuario?> = _user2

    private val _botonesActivados = MutableLiveData<Boolean>()
    val botonesActivados: LiveData<Boolean> = _botonesActivados

    private  val _sumandoPuntos = MutableLiveData<Boolean>()
    val sumandoPuntos: LiveData<Boolean> = _sumandoPuntos

    fun iniciarPartida(partida: Partida) {
            val partidaSinId = hashMapOf(
                "estado" to partida.estado,
                "dificultad" to partida.dificultad,
                "user1" to partida.user1,
                "user2" to partida.user2,
                "puntos_user1" to partida.puntos_user1,
                "puntos_user2" to partida.puntos_user2,
                "estado_user_1" to partida.estado_user_1,
                "estado_user_2" to partida.estado_user_2,
                "idGanador" to partida.idGanador
            )
            db.collection(Colecciones.colPartidas)
                .add(partidaSinId)
                .addOnSuccessListener {
                    _partida.value = Partida(it.id, partida.estado, partida.dificultad, partida.user1, partida.user2,
                        partida.puntos_user1, partida.puntos_user2, partida.estado_user_1, partida.estado_user_2, partida.idGanador)
                    _botonesActivados.value = true
                }
                .addOnFailureListener { e ->
                    Log.e("Carlos", "Error adding document")
                }
    }

    fun buscarSiHayUnaPartidaPendiente(idUsuario: String) {
        db.collection(Colecciones.colPartidas)
            .whereEqualTo("estado", 0)
            .whereEqualTo("user1", idUsuario)
            .whereEqualTo("user2", "0")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val documento = result.documents[0]
                    val partida = documento.toObject(Partida::class.java)
                    _partidaPendiente.value = true
                    partida!!.id = documento.id
                    _partida.value = partida
                    Log.e("Carlos", partida.toString())
                }
                else{
                    _partidaPendiente.value = false
                }
            }
    }

    fun obtenerJugadores(idUser1: String, idUser2: String) {
        db.collection(Colecciones.colUsuarios).document(idUser1)
            .get()
            .addOnSuccessListener {
                _user1.value = it.toObject(Usuario::class.java)
            }
        db.collection(Colecciones.colUsuarios).document(idUser2)
            .get()
            .addOnSuccessListener {
                _user2.value = it.toObject(Usuario::class.java)
            }
    }

    fun terminarPartida(){
        _botonesActivados.value = false
        if (_partida.value!!.puntos_user1 == 3) {
            _partida.value!!.estado = 1
        }else {
            _partida.value!!.estado = 2
        }
        val partida = _partida.value!!

        val partidaSinId = hashMapOf(
            "estado" to partida.estado,
            "dificultad" to partida.dificultad,
            "user1" to partida.user1,
            "user2" to partida.user2,
            "puntos_user1" to partida.puntos_user1,
            "puntos_user2" to partida.puntos_user2,
            "estado_user_1" to partida.estado_user_1,
            "estado_user_2" to partida.estado_user_2,
            "idGanador" to partida.idGanador
        )
        db.collection(Colecciones.colPartidas)
            .document(partida.id)
            .set(partidaSinId)
            .addOnSuccessListener {
                Log.e("Carlos", "Partida guardada")
            }
            .addOnFailureListener { e ->
                Log.w("Carlos", "Error adding document", e.cause)
            }
    }

    fun btnTerminarPartida(){
        _partida.value = null
        _botonesActivados.value = true
    }

    fun sumarPuntoUser1(){
        _partida.value!!.puntos_user1++
        _sumandoPuntos.value = true
        _botonesActivados.value = false

        val partida = _partida.value!!
        val partidaSinId = hashMapOf(
            "estado" to partida.estado,
            "dificultad" to partida.dificultad,
            "user1" to partida.user1,
            "user2" to partida.user2,
            "puntos_user1" to partida.puntos_user1,
            "puntos_user2" to partida.puntos_user2,
            "estado_user_1" to partida.estado_user_1,
            "estado_user_2" to partida.estado_user_2,
            "idGanador" to partida.idGanador
        )

        db.collection(Colecciones.colPartidas)
            .document(partida.id)
            .set(partidaSinId)
            .addOnSuccessListener {
                _botonesActivados.value = true
                _sumandoPuntos.value = false
                Log.e("Carlos", "Documento añadido.")
            }
            .addOnFailureListener { e ->
                Log.w("Carlos", "Error adding document", e.cause)
            }
    }

    fun sumarPuntoUser2(){
        _partida.value!!.puntos_user2++
        _sumandoPuntos.value = true
        _botonesActivados.value = false

        val partida = _partida.value!!
        val partidaSinId = hashMapOf(
            "estado" to partida.estado,
            "dificultad" to partida.dificultad,
            "user1" to partida.user1,
            "user2" to partida.user2,
            "puntos_user1" to partida.puntos_user1,
            "puntos_user2" to partida.puntos_user2,
            "estado_user_1" to partida.estado_user_1,
            "estado_user_2" to partida.estado_user_2,
            "idGanador" to partida.idGanador
        )

        db.collection(Colecciones.colPartidas)
            .document(partida.id)
            .set(partidaSinId)
            .addOnSuccessListener {
                _botonesActivados.value = true
                _sumandoPuntos.value = false
                Log.e("Carlos", "Documento añadido.")
            }
            .addOnFailureListener { e ->
                Log.w("Carlos", "Error adding document", e.cause)
            }
    }

    fun cerrarPartida(){
        _partida.value = null
        _partidaPendiente.value = null
        _user1.value = null
        _user2.value = null
        _botonesActivados.value = true
    }


}