package com.example.piedrapapeltijera.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Invitacion
import com.example.piedrapapeltijera.modelos.Partida
import com.example.piedrapapeltijera.modelos.Usuario
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class ListaUsuariosViewModel:ViewModel() {
    private val db = Firebase.firestore

    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    private val _partidasPendientes = MutableLiveData<List<Partida>>()
    val partidasPendientes: LiveData<List<Partida>> = _partidasPendientes

    private val _invitacionesPendientes = MutableLiveData<List<Invitacion>>()
    val invitacionesPendientes: LiveData<List<Invitacion>> = _invitacionesPendientes

    fun cargarUsuarios(idUsuario: String){
        db.collection(Colecciones.colUsuarios)
            .whereNotIn(FieldPath.documentId(), arrayListOf(idUsuario, "0"))
            .get()
            .addOnSuccessListener { result ->
                val usuarios = arrayListOf<Usuario>()
                result.documents.forEach { document ->
                    usuarios.add(document.toObject(Usuario::class.java)!!.copy(id = document.id))
                }
                _usuarios.value = usuarios
            }
            .addOnFailureListener{e->
                Log.e("Carlos", "Error al sacar usuarios", e)
            }
    }

    fun cargarPartidasPendientes(idUsuario: String){
        val partidasRef = db.collection(Colecciones.colPartidas)

        val query1 = partidasRef.whereEqualTo("user1.id", idUsuario)
            .whereEqualTo("estado", 0)
            .get()

        val query2 = partidasRef.whereEqualTo("user2.id", idUsuario)
            .whereEqualTo("estado", 0)
            .get()

        Tasks.whenAllSuccess<QuerySnapshot>(query1, query2)
            .addOnSuccessListener { results ->
                val result1 = results[0]
                val result2 = results[1]
                val partidas = arrayListOf<Partida>()
                result1.documents.forEach { document ->
                    partidas.add(document.toObject(Partida::class.java)!!.copy(id = document.id))
                }
                result2.documents.forEach { document ->
                    partidas.add(document.toObject(Partida::class.java)!!.copy(id = document.id))
                }

                _partidasPendientes.value = partidas
            }
            .addOnFailureListener{e->
                Log.e("Carlos", "Error al sacar partidas pendientes", e)
            }
    }

    fun cargarInvitacionesPendientes(idUsuario: String){
        val invitacionesRef = db.collection(Colecciones.colInvitaciones)

        val query1 = invitacionesRef.whereEqualTo("user_recibe.id", idUsuario)
            .whereEqualTo("estado", 0)
            .get()

        val query2 = invitacionesRef.whereEqualTo("user_envia.id", idUsuario)
            .whereEqualTo("estado", 0)
            .get()

        Tasks.whenAllSuccess<QuerySnapshot>(query1, query2)
            .addOnSuccessListener { results ->
                val invitacionesPendientes = results[0].toObjects(Invitacion::class.java)
                invitacionesPendientes.addAll(results[1].toObjects(Invitacion::class.java))
                Log.d("Carlos", "Invitaciones:"+invitacionesPendientes.toString())
                _invitacionesPendientes.value = invitacionesPendientes
            }
            .addOnFailureListener{e->
                Log.e("Carlos", "Error al sacar invitaciones pendientes", e)
            }
    }

    fun enviarInvitacion(invitacion: Invitacion) {
        val invitacionSinId = hashMapOf(
            "user_envia" to invitacion.user_envia,
            "user_recibe" to invitacion.user_recibe,
            "estado" to invitacion.estado
        )
        db.collection(Colecciones.colInvitaciones)
            .add(invitacionSinId)
            .addOnSuccessListener { documentReference ->
                cargarInvitacionesPendientes(invitacion.user_envia.get("id")!!)
                Log.d("Carlos", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Carlos", "Error adding document", e)
            }

    }
}