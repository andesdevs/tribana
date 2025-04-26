package com.cuatroraices.appnets.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuatroraices.appnets.R
import com.cuatroraices.appnets.Oferta
import com.cuatroraices.appnets.OfertaAdapter
import com.cuatroraices.appnets.PostulacionesResponse
import com.cuatroraices.appnets.RetrofitInstance
import org.chromium.base.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.ViewModelProvider
import com.cuatroraices.appnets.ui.home.SharedViewModel

// 📌 Asegúrate de que esta interfaz existe en tu código.
interface RefreshListener {
    fun onRefreshNeeded()
}

class HomeFragment : Fragment(), RefreshListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ofertaAdapter: OfertaAdapter
    private lateinit var usuarioEmail: String

    override fun onRefreshNeeded() {
        obtenerPostulacionesUsuario() // 🔹 Se ejecutará tras una nueva postulación
    }

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // ✅ Inicializar correctamente el ViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewOfertas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        usuarioEmail = sharedPreferences.getString("email", "") ?: ""

        // ✅ Asegurarse de que sharedViewModel está inicializado antes de observarlo
        sharedViewModel.actualizarPostulaciones.observe(viewLifecycleOwner) { actualizar ->
            if (actualizar) {
                obtenerPostulacionesUsuario()
                sharedViewModel.resetearActualizacion() // Resetea el estado después de actualizar
            }
        }

        obtenerOfertas()
        obtenerPostulacionesUsuario()

        return view
    }

    private fun obtenerOfertas() {
        val callOfertas = RetrofitInstance.api.getOfertas()
        val callPostulaciones = RetrofitInstance.api.obtenerPostulacionesUsuario(usuarioEmail)

        callPostulaciones.enqueue(object : Callback<PostulacionesResponse> {
            override fun onResponse(call: Call<PostulacionesResponse>, response: Response<PostulacionesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaPostulaciones = response.body()!!.postulaciones

                    callOfertas.enqueue(object : Callback<List<Oferta>> {
                        override fun onResponse(call: Call<List<Oferta>>, response: Response<List<Oferta>>) {
                            if (response.isSuccessful && response.body() != null) {
                                val listaOfertas = response.body()!!
                                val listaPostulacionesSegura = listaPostulaciones ?: emptyList()

                                // 🔹 Separar y ROTAR las ofertas según su categoría
                                val destacadas = listaOfertas.filter { it.categoria == "destacada" }.shuffled().take(10)
                                val pro = listaOfertas.filter { it.categoria == "pro" }.shuffled().take(20)
                                val basicas = listaOfertas.filter { it.categoria == "basica" }.shuffled()

                                // 🔥 Unir en orden correcto
                                val ofertasOrdenadas = destacadas + pro + basicas

                                // ✅ Mantener funcionalidad previa con las postulaciones
                                ofertaAdapter = OfertaAdapter(ofertasOrdenadas, listaPostulacionesSegura)
                                recyclerView.adapter = ofertaAdapter
                            } else {
                                Toast.makeText(requireContext(), "No se encontraron ofertas", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<Oferta>>, t: Throwable) {
                            Toast.makeText(requireContext(), "Error de conexión al obtener ofertas: ${t.message}", Toast.LENGTH_LONG).show()
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


    /*private fun obtenerOfertas() {
        val callOfertas = RetrofitInstance.api.getOfertas()
        val callPostulaciones = RetrofitInstance.api.obtenerPostulacionesUsuario(usuarioEmail)

        callPostulaciones.enqueue(object : Callback<PostulacionesResponse> {
            override fun onResponse(call: Call<PostulacionesResponse>, response: Response<PostulacionesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaPostulaciones = response.body()!!.postulaciones

                    callOfertas.enqueue(object : Callback<List<Oferta>> {
                        override fun onResponse(call: Call<List<Oferta>>, response: Response<List<Oferta>>) {
                            if (response.isSuccessful && response.body() != null) {
                                val listaOfertas = response.body()!!
                                val listaPostulacionesSegura = listaPostulaciones ?: emptyList()
                                ofertaAdapter = OfertaAdapter(listaOfertas, listaPostulacionesSegura)
                                //ofertaAdapter = OfertaAdapter(listaOfertas, listaPostulaciones)
                                recyclerView.adapter = ofertaAdapter
                            } else {
                                Toast.makeText(requireContext(), "No se encontraron ofertas", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<Oferta>>, t: Throwable) {
                            Toast.makeText(requireContext(), "Error de conexión al obtener ofertas: ${t.message}", Toast.LENGTH_LONG).show()
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
    }*/

    private fun obtenerPostulacionesUsuario() {
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val emailUsuario = sharedPreferences.getString("email", "") ?: ""
        val rutUsuario = sharedPreferences.getString("rut", "") ?: ""

        if (emailUsuario.isEmpty() || rutUsuario.isEmpty()) {
            Log.e("HomeFragment", "No se encontraron datos del usuario en SharedPreferences")
            return
        }

        RetrofitInstance.api.verificarPostulaciones(emailUsuario, rutUsuario).enqueue(object : Callback<PostulacionesResponse> {
            override fun onResponse(call: Call<PostulacionesResponse>, response: Response<PostulacionesResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val postulaciones = response.body()?.postulaciones ?: emptyList()

                    val prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit()
                    prefs.putStringSet("postulaciones", postulaciones.map { it.toString() }.toSet())
                    prefs.apply()

                    Log.d("HomeFragment", "Postulaciones guardadas: $postulaciones")

                    obtenerOfertas()  // 🔹 Actualiza ofertas tras obtener postulaciones
                } else {
                    Log.e("HomeFragment", "Error en la respuesta de postulaciones")
                }
            }

            override fun onFailure(call: Call<PostulacionesResponse>, t: Throwable) {
                Log.e("HomeFragment", "Error en la conexión al obtener postulaciones: ${t.message}")
            }
        })
    }
}

