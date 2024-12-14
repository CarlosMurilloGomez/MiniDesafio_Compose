package com.example.piedrapapeltijera.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Invitacion
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PartidaOnlineViewModel: ViewModel() {
    val db = Firebase.firestore

    private val _partida = MutableLiveData<Partida?>()
    val partida: LiveData<Partida?> = _partida

    fun cargarPartida(idPartida: String) {
        db.collection(Colecciones.colPartidas)
            .document(idPartida)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    _partida.value = it.toObject(Partida::class.java)!!.copy(id = it.id)
                }
            }
    }

    private val _botonesActivados = MutableLiveData<Boolean>()
    val botonesActivados: LiveData<Boolean> = _botonesActivados

    fun activarBotones(){
        _botonesActivados.value = true
    }

    private  val _sumandoPuntos = MutableLiveData<Boolean>()
    val sumandoPuntos: LiveData<Boolean> = _sumandoPuntos



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
            "estado_ronda" to partida.estado_ronda,
            "estado_user_1" to partida.estado_user_1,
            "estado_user_2" to partida.estado_user_2
        )

        db.collection(Colecciones.colPartidas)
            .document(partida.id)
            .set(partidaSinId)
            .addOnSuccessListener {
                _botonesActivados.value = true
                _sumandoPuntos.value = false
                cargarPartida(_partida.value!!.id)
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
            "estado_ronda" to partida.estado_ronda,
            "estado_user_1" to partida.estado_user_1,
            "estado_user_2" to partida.estado_user_2
        )

        db.collection(Colecciones.colPartidas)
            .document(partida.id)
            .set(partidaSinId)
            .addOnSuccessListener {
                _botonesActivados.value = true
                _sumandoPuntos.value = false
                cargarPartida(_partida.value!!.id)
            }
            .addOnFailureListener { e ->
                Log.w("Carlos", "Error adding document", e.cause)
            }
    }

    fun cerrarPartida(){
        _partida.value = null
        _botonesActivados.value = true
    }

    fun btnListo(idUsuario: String){
        db.collection(Colecciones.colPartidas)
            .document(_partida.value!!.id)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    _partida.value = it.toObject(Partida::class.java)!!.copy(id = it.id)

                    if (partida.value!!.estado_ronda == 0) {
                        if (partida.value!!.user1.get("id") == idUsuario) {
                            _partida.value!!.estado_ronda = 1
                        } else {
                            _partida.value!!.estado_ronda = 2
                        }
                    } else {
                        _partida.value!!.estado_ronda = 0
                        _partida.value!!.estado_user_1 = 0
                        _partida.value!!.estado_user_2 = 0

                        _botonesActivados.value = true
                    }

                    db.collection(Colecciones.colPartidas)
                        .document(partida.value!!.id)
                        .set(partida.value!!)
                        .addOnSuccessListener {
                            cargarPartida(_partida.value!!.id)
                            Log.e("Carlos", "Documento actualizado.!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Carlos", "Error al actualizar el documento.", e)
                        }
                }
            }
    }

    fun jugar(user: Int, jugada: Int){
        _sumandoPuntos.value = true
        db.collection(Colecciones.colPartidas)
            .document(_partida.value!!.id)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    _partida.value = it.toObject(Partida::class.java)!!.copy(id = it.id)

                    if (user == 1){
                        _partida.value!!.estado_user_1 = jugada
                    }else{
                        _partida.value!!.estado_user_2 = jugada
                    }
                    db.collection(Colecciones.colPartidas)
                        .document(partida.value!!.id)
                        .set(partida.value!!)
                        .addOnSuccessListener {
                            if (user == 1 && partida.value!!.estado_user_2 != 0) {
                                when (jugada){
                                    1 -> if (partida.value!!.estado_user_2 == 2) sumarPuntoUser2() else if (partida.value!!.estado_user_2 == 3) sumarPuntoUser1()
                                    2 -> if (partida.value!!.estado_user_2 == 3) sumarPuntoUser2() else if (partida.value!!.estado_user_2 == 1) sumarPuntoUser1()
                                    3 -> if (partida.value!!.estado_user_2 == 1) sumarPuntoUser2() else if (partida.value!!.estado_user_2 == 2) sumarPuntoUser1()
                                }
                            }else if (user == 2 && partida.value!!.estado_user_1 != 0) {
                                when (jugada){
                                    1 -> if (partida.value!!.estado_user_1 == 2) sumarPuntoUser1() else if (partida.value!!.estado_user_1 == 3) sumarPuntoUser2()
                                    2 -> if (partida.value!!.estado_user_1 == 3) sumarPuntoUser1() else if (partida.value!!.estado_user_1 == 1) sumarPuntoUser2()
                                    3 -> if (partida.value!!.estado_user_1 == 1) sumarPuntoUser1() else if (partida.value!!.estado_user_1 == 2) sumarPuntoUser2()
                                    else -> cargarPartida(_partida.value!!.id)
                                }
                            }else{
                                cargarPartida(_partida.value!!.id)
                            }
                            _sumandoPuntos.value = false
                            Log.e("Carlos", "Documento actualizado.!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Carlos", "Error al actualizar el documento.", e)
                        }
                }
            }

    }

    fun revancha(idUsuario: String, nombreUsuario: String, contexto:Context){

        var invitacion:Invitacion;

        if (partida.value!!.user1.get("id") == idUsuario){
            db.collection(Colecciones.colInvitaciones)
                .whereEqualTo("user_envia.id", partida.value!!.user2.get("id"))
                .whereEqualTo("user_recibe.id", idUsuario)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        invitacion = Invitacion(user_envia = hashMapOf("id" to idUsuario, "nombre" to nombreUsuario), user_recibe = hashMapOf("id" to partida.value!!.user2.get("id")!!, "nombre" to partida.value!!.user2.get("nombre")!!))
                        db.collection(Colecciones.colInvitaciones)
                            .add(invitacion)
                            .addOnSuccessListener {
                                Toast.makeText(contexto, "Invitacion de revancha enviada", Toast.LENGTH_SHORT).show()
                                Log.e("Carlos", "Documento creado.!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Carlos", "Error al crear el documento.", e)
                            }
                    }
                    else {
                        Toast.makeText(contexto, "Ya existe una invitacion de revancha", Toast.LENGTH_SHORT).show()
                    }
                }
        }else {
            db.collection(Colecciones.colInvitaciones)
                .whereEqualTo("user_envia.id", partida.value!!.user1.get("id"))
                .whereEqualTo("user_recibe.id", idUsuario)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        invitacion = Invitacion(
                            user_envia = hashMapOf(
                                "id" to idUsuario,
                                "nombre" to nombreUsuario
                            ),
                            user_recibe = hashMapOf(
                                "id" to partida.value!!.user1.get("id")!!,
                                "nombre" to partida.value!!.user1.get("nombre")!!
                            )
                        )
                        db.collection(Colecciones.colInvitaciones)
                            .add(invitacion)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    contexto,
                                    "Invitacion de revancha enviada",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("Carlos", "Documento creado.!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Carlos", "Error al crear el documento.", e)
                            }
                    }
                    else {
                        Toast.makeText(contexto, "Ya existe una invitacion de revancha", Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }

}