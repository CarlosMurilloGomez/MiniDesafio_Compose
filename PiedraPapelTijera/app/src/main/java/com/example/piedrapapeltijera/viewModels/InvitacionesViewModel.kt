package com.example.piedrapapeltijera.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Invitacion
import com.example.piedrapapeltijera.parametros.Colecciones
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class InvitacionesViewModel: ViewModel() {
    private var db = Firebase.firestore

    private val _invitaciones = MutableLiveData<List<Invitacion>>()
    val invitaciones: MutableLiveData<List<Invitacion>> = _invitaciones

    fun buscarInvitaciones(idUsuario: String){
        db.collection(Colecciones.colInvitaciones)
            .whereEqualTo("user_recibe.id", idUsuario)
            .whereEqualTo("estado", 0)
            .get()
            .addOnSuccessListener { result ->
                var invitaciones = arrayListOf<Invitacion>()
                result.documents.forEach { document ->
                    invitaciones.add(document.toObject(Invitacion::class.java)!!.copy(id = document.id))
                }
                _invitaciones.value = invitaciones
            }
    }

    fun borrarInvitacion(invitacion: Invitacion) {
        db.collection(Colecciones.colInvitaciones)
            .document(invitacion.id)
            .delete()
            .addOnSuccessListener {
                buscarInvitaciones(invitacion.user_recibe.get("id")!!)
            }
    }
}