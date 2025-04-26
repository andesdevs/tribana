package com.cuatroraices.appnets.ui.redapoyo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RedApoyoViewModel : ViewModel() {

    // LiveData con la lista de recursos
    private val _recursos = MutableLiveData<List<String>>()
    val recursos: LiveData<List<String>> = _recursos

    /** Simula la carga de contenidos; en tu caso podrías llamar a un repositorio o API */
    fun cargarRecursos() {
        _recursos.value = listOf(
            "Guía de búsqueda de empleo para cuidadoras",
            "Video: Preparación para entrevistas",
            "Enlace a lista de cursos gratuitos",
            "Consejos de networking y cómo utilizarlos",
            "Plantillas de CV y cartas de presentación"
        )
    }
}
