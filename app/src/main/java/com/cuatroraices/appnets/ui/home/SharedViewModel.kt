package com.cuatroraices.appnets.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _actualizarPostulaciones = MutableLiveData<Boolean>()
    val actualizarPostulaciones: LiveData<Boolean> get() = _actualizarPostulaciones

    // Método para activar la actualización
    fun solicitarActualizacion() {
        _actualizarPostulaciones.value = true
    }

    // Método para resetear el estado después de actualizar
    fun resetearActualizacion() {
        _actualizarPostulaciones.value = false
    }
}