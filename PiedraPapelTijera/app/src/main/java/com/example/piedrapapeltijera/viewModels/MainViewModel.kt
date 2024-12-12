package com.example.piedrapapeltijera.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piedrapapeltijera.modelos.Usuario

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

    fun cerrarSesion() {
        _usuarioLogeado.value = null
    }

}