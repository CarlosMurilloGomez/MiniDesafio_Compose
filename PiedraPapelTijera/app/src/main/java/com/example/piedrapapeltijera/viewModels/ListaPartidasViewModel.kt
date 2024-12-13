package com.example.piedrapapeltijera.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListaPartidasViewModel: ViewModel() {
    private val db = Firebase.firestore

    private val _partidas = MutableLiveData<List<Partida>>()
    val partidas: LiveData<List<Partida>> = _partidas

    fun cargarPartidas(idUsuario: String) {
        val partidasRef = db.collection(Colecciones.colPartidas)

        val query1 = partidasRef.whereEqualTo("user1.id", idUsuario)
            .whereNotEqualTo("estado", 0)
            .get()

        val query2 = partidasRef.whereEqualTo("user2.id", idUsuario)
            .whereNotEqualTo("estado", 0)
            .get()
        Tasks.whenAllSuccess<QuerySnapshot>(query1, query2)
            .addOnSuccessListener { results ->
                val listaPartidas = ArrayList<Partida>()
                for (result in results) {
                    for (document in result) {
                        val partida = document.toObject(Partida::class.java)
                        listaPartidas.add(partida.copy(id = document.id))
                    }
                }
                _partidas.value = listaPartidas.sortedWith(compareBy(
                    { LocalDate.parse(it.fecha_hora.get("fecha"), DateTimeFormatter.ofPattern("dd/MM/yyyy")) },
                    { it.fecha_hora.get("hora") })).reversed()
            }
            .addOnFailureListener { exception ->
                Log.w("Carlos", "Error getting documents.", exception)
            }
    }

    fun cargarGanadas(idUsuario: String) {
        val partidasRef = db.collection(Colecciones.colPartidas)

        val query1 = partidasRef.whereEqualTo("user1.id", idUsuario)
            .whereEqualTo("estado", 1)
            .get()

        val query2 = partidasRef.whereEqualTo("user2.id", idUsuario)
            .whereEqualTo("estado", 2)
            .get()
        Tasks.whenAllSuccess<QuerySnapshot>(query1, query2)
            .addOnSuccessListener { results ->
                val listaPartidas = ArrayList<Partida>()
                for (result in results) {
                    for (document in result) {
                        val partida = document.toObject(Partida::class.java)
                        listaPartidas.add(partida.copy(id = document.id))
                    }
                }
                _partidas.value = listaPartidas.sortedWith(compareBy(
                    { LocalDate.parse(it.fecha_hora.get("fecha"), DateTimeFormatter.ofPattern("dd/MM/yyyy")) },
                    { it.fecha_hora.get("hora") })).reversed()
            }
            .addOnFailureListener { exception ->
                Log.w("Carlos", "Error getting documents.", exception)
            }
    }

    fun cargarPerdidas(idUsuario: String) {
        val partidasRef = db.collection(Colecciones.colPartidas)

        val query1 = partidasRef.whereEqualTo("user1.id", idUsuario)
            .whereEqualTo("estado", 2)
            .get()

        val query2 = partidasRef.whereEqualTo("user2.id", idUsuario)
            .whereEqualTo("estado", 1)
            .get()
        Tasks.whenAllSuccess<QuerySnapshot>(query1, query2)
            .addOnSuccessListener { results ->
                val listaPartidas = ArrayList<Partida>()
                for (result in results) {
                    for (document in result) {
                        val partida = document.toObject(Partida::class.java)
                        listaPartidas.add(partida.copy(id = document.id))
                    }
                }
                _partidas.value = listaPartidas.sortedWith(compareBy(
                    { LocalDate.parse(it.fecha_hora.get("fecha"), DateTimeFormatter.ofPattern("dd/MM/yyyy")) },
                    { it.fecha_hora.get("hora") })).reversed()
            }
            .addOnFailureListener { exception ->
                Log.w("Carlos", "Error getting documents.", exception)
            }
    }


}