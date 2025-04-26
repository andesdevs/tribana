package com.cuatroraices.appnets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.chromium.base.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OfertasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ofertaAdapter: OfertaAdapter
    private lateinit var usuarioEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewOfertas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obtener email del usuario desde SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        usuarioEmail = sharedPreferences.getString("email", "") ?: ""

        // Llamar a la función para obtener ofertas y postulaciones
        obtenerOfertasYPostulaciones()

        return view
    }

    private fun obtenerOfertasYPostulaciones() {
        val api = RetrofitInstance.api

        // Llamada para obtener las postulaciones del usuario
        api.obtenerPostulacionesUsuario(usuarioEmail).enqueue(object : Callback<PostulacionesResponse> {
            override fun onResponse(call: Call<PostulacionesResponse>, response: Response<PostulacionesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaPostulaciones = response.body()!!.postulaciones // IDs de ofertas ya postuladas

                    // Ahora obtenemos las ofertas
                    api.getOfertas().enqueue(object : Callback<List<Oferta>> {
                        override fun onResponse(call: Call<List<Oferta>>, response: Response<List<Oferta>>) {
                            if (response.isSuccessful && response.body() != null) {
                                val listaOfertas = response.body()!!

                                // **Pasamos las postulaciones al adaptador**
                                ofertaAdapter = OfertaAdapter(listaOfertas, listaPostulaciones)
                                recyclerView.adapter = ofertaAdapter

                            } else {
                                Toast.makeText(requireContext(), "No se encontraron ofertas", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<Oferta>>, t: Throwable) {
                            Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "No se pudieron obtener postulaciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostulacionesResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión al obtener postulaciones: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
