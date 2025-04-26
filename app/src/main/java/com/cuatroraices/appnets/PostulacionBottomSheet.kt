package com.cuatroraices.appnets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.cuatroraices.appnets.databinding.BottomSheetPostulacionBinding
import androidx.lifecycle.ViewModelProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.cuatroraices.appnets.ui.home.SharedViewModel
import org.chromium.base.Log


class PostulacionBottomSheet(private val oferta: Oferta) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPostulacionBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPostulacionBinding.inflate(inflater, container, false)

        // 🔹 Inicializar el SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 LOGS PARA DEPURAR
        Log.d("PostulacionBottomSheet", "Oferta seleccionada ID: ${oferta.id}")
        Log.d("PostulacionBottomSheet", "Empresa: ${oferta.nombre_empresa}")
        Log.d("PostulacionBottomSheet", "Sueldo: ${oferta.remuneracion}")
        Log.d("PostulacionBottomSheet", "Descripción larga: ${oferta.descripcion_larga}")

        // ✅ Mostrar los datos en la interfaz
        binding.tvTituloOferta.text = oferta.titulo
        binding.tvDescripcionOferta.text = oferta.descripcion_larga // 🔹 Mostrar la descripción larga
        binding.tvRemuneracionOferta.text = "Sueldo: ${oferta.remuneracion} CLP" // 🔹 Mostrar la remuneración

        // Obtener usuario desde SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val rutUsuario = sharedPreferences.getString("rut", "") ?: ""
        val emailUsuario = sharedPreferences.getString("email", "") ?: ""

        binding.btnPostular.setOnClickListener {
            enviarPostulacion(oferta.id, rutUsuario, emailUsuario)
        }
    }

    private fun enviarPostulacion(idOferta: Int, rut: String, email: String) {
        if (rut.isEmpty() || email.isEmpty()) {
            mostrarMensaje("No se encontraron datos del usuario")
            return
        }

        val call = RetrofitInstance.api.postularOferta(idOferta, rut, email)
        call.enqueue(object : Callback<PostulacionResponse> {
            override fun onResponse(call: Call<PostulacionResponse>, response: Response<PostulacionResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    mostrarMensaje("Postulación realizada con éxito")

                    // 🔹 Notificar al ViewModel para actualizar la lista de postulaciones
                    sharedViewModel.solicitarActualizacion()

                    dismiss() // Cierra el BottomSheet
                } else {
                    mostrarMensaje("No se pudo realizar la postulación")
                }
            }

            override fun onFailure(call: Call<PostulacionResponse>, t: Throwable) {
                mostrarMensaje("Error en la conexión: ${t.message}")
            }
        })
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


/*
package com.cuatroraices.appnets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.cuatroraices.appnets.databinding.BottomSheetPostulacionBinding
import com.cuatroraices.appnets.Oferta
import com.cuatroraices.appnets.PostulacionResponse
import com.cuatroraices.appnets.RetrofitInstance
import com.cuatroraices.appnets.ui.home.HomeFragment
import com.cuatroraices.appnets.ui.home.RefreshListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.ViewModelProvider
import com.cuatroraices.appnets.ui.home.SharedViewModel


class PostulacionBottomSheet(private val oferta: Oferta) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPostulacionBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPostulacionBinding.inflate(inflater, container, false)

        // 🔹 Inicializar el SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mostrar datos en la interfaz
        binding.tvTituloOferta.text = oferta.titulo
        binding.tvDescripcionOferta.text = oferta.descripcion

        // Obtener usuario desde SharedPreferences (Asegúrate de que esté guardado)
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val rutUsuario = sharedPreferences.getString("rut", "") ?: ""
        val emailUsuario = sharedPreferences.getString("email", "") ?: ""

        binding.btnPostular.setOnClickListener {
            enviarPostulacion(oferta.id, rutUsuario, emailUsuario)
        }
    }

    private fun enviarPostulacion(idOferta: Int, rut: String, email: String) {
        if (rut.isEmpty() || email.isEmpty()) {
            mostrarMensaje("No se encontraron datos del usuario")
            return
        }

        val call = RetrofitInstance.api.postularOferta(idOferta, rut, email)
        call.enqueue(object : Callback<PostulacionResponse> {
            override fun onResponse(call: Call<PostulacionResponse>, response: Response<PostulacionResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    mostrarMensaje("Postulación realizada con éxito")

                    // 🔹 Notificar a HomeFragment para actualizar la lista de postulaciones
                    sharedViewModel.solicitarActualizacion()

                    dismiss() // Cierra el BottomSheet
                } else {
                    mostrarMensaje("No se pudo realizar la postulación")
                }
            }

            override fun onFailure(call: Call<PostulacionResponse>, t: Throwable) {
                mostrarMensaje("Error en la conexión: ${t.message}")
            }
        })
    }


    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
*/
