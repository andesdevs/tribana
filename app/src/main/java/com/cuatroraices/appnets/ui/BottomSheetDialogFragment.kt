package com.cuatroraices.appnets.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.cuatroraices.appnets.R
import com.cuatroraices.appnets.Oferta
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PostulacionBottomSheet(private val oferta: Oferta) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_postulacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTituloOferta = view.findViewById<TextView>(R.id.tvTituloOferta)
        val tvDescripcionOferta = view.findViewById<TextView>(R.id.tvDescripcionOferta)
        val btnPostular = view.findViewById<Button>(R.id.btnPostular)

        tvTituloOferta.text = oferta.titulo
        tvDescripcionOferta.text = oferta.descripcion

        btnPostular.setOnClickListener {
            enviarPostulacion(oferta)
            dismiss() // Cierra el BottomSheet después de postular
        }
    }

    private fun enviarPostulacion(oferta: Oferta) {
        Toast.makeText(requireContext(), "Postulado a: ${oferta.titulo}", Toast.LENGTH_SHORT).show()
    }
}
