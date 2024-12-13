package com.example.piedrapapeltijera.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PartidaOfflineViewModel: ViewModel() {
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
            .addOnSuccessListener {
                _partida.value = null
                Log.e("Carlos", "Documento borrado.!")
            }
            .addOnFailureListener { e -> Log.w("Carlos", "Error al borrar el documento.", e) }
    }

    private val _partida = MutableLiveData<Partida?>()
    val partida: LiveData<Partida?> = _partida

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
            )
            db.collection(Colecciones.colPartidas)
                .add(partidaSinId)
                .addOnSuccessListener {
                    _partida.value = Partida(it.id, partida.estado, partida.dificultad, partida.user1, partida.user2,
                        partida.puntos_user1, partida.puntos_user2)
                    _botonesActivados.value = true
                }
                .addOnFailureListener { e ->
                    Log.e("Carlos", "Error adding document")
                }
    }

    fun buscarSiHayUnaPartidaPendiente(idUsuario: String) {
        db.collection(Colecciones.colPartidas)
            .whereEqualTo("estado", 0)
            .whereEqualTo("user1.id", idUsuario)
            .whereEqualTo("user2.id", "0")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val documento = result.documents[0]
                    val partida = documento.toObject(Partida::class.java)
                    _partidaPendiente.value = true
                    partida!!.id = documento.id
                    _partida.value = partida
                }
                else{
                    _partidaPendiente.value = false
                }
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


        val fechaHoraHoy = hashMapOf("fecha" to LocalDateTime.now(ZoneId.of("Europe/Madrid")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                    "hora" to LocalDateTime.now( ZoneId.of("Europe/Madrid")).format(DateTimeFormatter.ofPattern("HH:mm:ss")))

        val partidaSinId = hashMapOf(
            "estado" to partida.estado,
            "dificultad" to partida.dificultad,
            "user1" to partida.user1,
            "user2" to partida.user2,
            "puntos_user1" to partida.puntos_user1,
            "puntos_user2" to partida.puntos_user2,
            "fecha_hora" to fechaHoraHoy
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
        )

        db.collection(Colecciones.colPartidas)
            .document(partida.id)
            .set(partidaSinId)
            .addOnSuccessListener {
                _botonesActivados.value = true
                _sumandoPuntos.value = false
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
        )

        db.collection(Colecciones.colPartidas)
            .document(partida.id)
            .set(partidaSinId)
            .addOnSuccessListener {
                _botonesActivados.value = true
                _sumandoPuntos.value = false
            }
            .addOnFailureListener { e ->
                Log.w("Carlos", "Error adding document", e.cause)
            }
    }

    fun cerrarPartida(){
        _partida.value = null
        _partidaPendiente.value = null
        _botonesActivados.value = true
    }


}