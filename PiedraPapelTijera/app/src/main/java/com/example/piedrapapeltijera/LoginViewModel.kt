package com.example.piedrapapeltijera

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LoginViewModel:ViewModel() {
    val db = Firebase.firestore

    private val _usuarios = mutableStateOf<List<Usuario>>(emptyList())
    val usuarios: State<List<Usuario>> = _usuarios

    private val _login = MutableLiveData<Int>()
    val login: LiveData<Int> = _login

    private val _registrado = MutableLiveData<Boolean?>()
    val registrado: LiveData<Boolean?> = _registrado


    fun registrar(usuario: Usuario) {
        var usuarioIgual = false
        for (u in _usuarios.value) {
            if (u.nombre.lowercase() == usuario.nombre.lowercase()) {
                usuarioIgual = true
            }
        }
        if (!usuarioIgual) {
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
                    cargarUsers()
                }
                .addOnFailureListener { e ->
                    Log.w("Carlos", "Error adding document", e)
                }
            _registrado.value = true
        }else{
            _registrado.value = false
        }

    }

    fun cargarUsers() {
        db.collection(Colecciones.colUsuarios)
            .get()
            .addOnSuccessListener { result ->
                val userList = result.mapNotNull { document ->
                    document.toObject(Usuario::class.java).apply {
                        id = document.id
                    }
                }
                _usuarios.value = userList
            }
            .addOnFailureListener { e ->
                Log.w("Carlos", "Error fetching users", e)
            }
    }

    fun iniciarSesion(usuario: String, password: String) {
        for (u in _usuarios.value) {
            if (u.nombre.lowercase() == usuario.lowercase()){
                if(u.password == password) {
                    _login.value = 3
                    break
                }else {
                    _login.value = 2
                    break
                }
            }
        }
        if (_login.value == 0) {
            _login.value = 1
        }
    }

    fun restablecerLogin() {
        _login.value = 0
    }

    fun restablecerRegistrado() {
        _registrado.value = null
    }
}