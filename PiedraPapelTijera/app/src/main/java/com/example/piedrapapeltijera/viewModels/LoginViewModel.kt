package com.example.piedrapapeltijera.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.parametros.Colecciones
import com.example.piedrapapeltijera.modelos.Usuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LoginViewModel:ViewModel() {
    val db = Firebase.firestore

    private val _login = MutableLiveData<Boolean?>()
    val login: LiveData<Boolean?> = _login

    private val _registrado = MutableLiveData<Boolean?>()
    val registrado: LiveData<Boolean?> = _registrado

    private val _usuarioLogeado = MutableLiveData<Usuario?>()
    val usuarioLogeado: MutableLiveData<Usuario?> = _usuarioLogeado


    fun registrar(usuario: Usuario) {
        db.collection(Colecciones.colUsuarios)
            .whereEqualTo("nombre", usuario.nombre)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    val usuarioSinId = hashMapOf(
                        "nombre" to usuario.nombre,
                        "password" to usuario.password,
                        "email" to usuario.email,
                        "fechaNac" to usuario.fechaNac
                    )
                    db.collection(Colecciones.colUsuarios)
                        .add(usuarioSinId)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Carlos", "DocumentSnapshot added with ID: ${documentReference.id}")
                            _usuarioLogeado.value = usuario.copy(id = documentReference.id)
                            _registrado.value = true
                        }
                        .addOnFailureListener { e ->
                            Log.e("Carlos", "Error adding document")
                        }
                }else{
                    _registrado.value = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("Carlos", "Error fetching users")
            }
    }


    fun iniciarSesion(usuario: String, password: String) {
        db.collection(Colecciones.colUsuarios)
            .whereEqualTo("nombre", usuario)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    document.toObject(Usuario::class.java).apply {
                        _usuarioLogeado.value = this!!.copy(id = document.id)
                    }
                    _login.value = true
                }else{
                    _login.value = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("Carlos", "Error fetching users")
            }


    }



    fun restablecerLogin() {
        _login.value = null
    }

    fun restablecerRegistrado() {
        _registrado.value = null
    }
}