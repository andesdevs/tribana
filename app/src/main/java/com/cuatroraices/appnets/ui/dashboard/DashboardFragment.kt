package com.cuatroraices.appnets.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cuatroraices.appnets.R
import com.cuatroraices.appnets.databinding.FragmentDashboardBinding
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // 🔹 Asegura que el contenido esté oculto y el preloader visible
        binding.progressBar.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        var rutUsuario = sharedPreferences.getString("rut", null)

        if (rutUsuario.isNullOrEmpty()) {
            // Si el RUT no está disponible, intentamos recargarlo en 1 segundo
            binding.root.postDelayed({
                val retryRut = sharedPreferences.getString("rut", null)
                if (!retryRut.isNullOrEmpty()) {
                    cargarDatos(retryRut)
                } else {
                    binding.txtError.visibility = View.VISIBLE
                    binding.txtError.text = "No se encontró el RUT del usuario"
                    binding.progressBar.visibility = View.GONE
                }
            }, 1000)
            return
        }

        // Cargar los datos si el RUT ya está disponible
        cargarDatos(rutUsuario)

        // Observamos los datos del usuario
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.txtWelcome.text = user.nombre

                val imageUrl = if (!user.foto_perfil.isNullOrEmpty() && user.foto_perfil.startsWith("http", ignoreCase = true)) {
                    user.foto_perfil
                } else if (!user.foto_perfil.isNullOrEmpty()) {
                    "https://www.cuatroraices.cl/api/${user.foto_perfil}"
                } else {
                    null
                }

                if (imageUrl != null) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_user)
                        .into(binding.imgUserPhoto)
                } else {
                    binding.imgUserPhoto.setImageResource(R.drawable.ic_user)
                }
            }
            verificarCargaCompleta()
        }

        // Observamos las estadísticas del Dashboard
        viewModel.dashboardData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.txtTotalPostulaciones.text = "${data.total_postulaciones} trabajos.-"
                binding.txtUltimaPostulacion.text = data.ultima_postulacion?.let {
                    "${it.titulo} - ${it.empresa}"
                } ?: "Última: No hay postulaciones"

                val formatCLP = NumberFormat.getNumberInstance(Locale("es", "CL"))
                val formattedGanancia = "$" + formatCLP.format(data.total_ganado)
                binding.txtTotalGanado.text = formattedGanancia
            }
            verificarCargaCompleta()
        }
    }

    /**
     * Carga los datos del usuario y del dashboard.
     */
    private fun cargarDatos(rutUsuario: String) {
        // Mostrar el ProgressBar y ocultar el contenido mientras se cargan los datos
        binding.progressBar.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE
        binding.txtError.visibility = View.GONE

        viewModel.loadUserData(rutUsuario)
        viewModel.loadDashboardData(rutUsuario)
    }

    /**
     * Verifica si los datos han sido cargados correctamente y oculta el ProgressBar.
     */
    private fun verificarCargaCompleta() {
        val userDataLoaded = viewModel.userData.value != null
        val dashboardDataLoaded = viewModel.dashboardData.value != null

        if (userDataLoaded && dashboardDataLoaded) {
            binding.progressBar.visibility = View.GONE
            binding.contentContainer.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


/*
package com.cuatroraices.appnets.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cuatroraices.appnets.R
import com.cuatroraices.appnets.databinding.FragmentDashboardBinding
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // 🔹 Ocultar el contenido y mostrar el ProgressBar
        binding.progressBar.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE // Asegura que el contenido se oculte mientras se cargan los datos


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Recupera el RUT del usuario desde SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val rutUsuario = sharedPreferences.getString("rut", null)

        if (rutUsuario.isNullOrEmpty()) {
            binding.txtError.visibility = View.VISIBLE
            binding.txtError.text = "No se encontró el RUT del usuario"
            intentarCargarDatos()
            binding.progressBar.visibility = View.GONE

            binding.root.postDelayed({
                val retryRut = sharedPreferences.getString("rut", null)
                if (!retryRut.isNullOrEmpty()) {
                    // Volvemos a mostrar el ProgressBar antes de recargar
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentContainer.visibility = View.GONE

                    // Carga los datos con el nuevo RUT
                    viewModel.loadUserData(retryRut)
                    viewModel.loadDashboardData(retryRut)
                } else {
                    // Si sigue siendo null, mostramos el error
                    binding.txtError.visibility = View.VISIBLE
                    binding.txtError.text = "No se encontró el RUT del usuario"
                    binding.progressBar.visibility = View.GONE
                }
            }, 500)
            return
        }

        // Cargar datos del usuario y estadísticas
        viewModel.loadUserData(rutUsuario)
        viewModel.loadDashboardData(rutUsuario)

        // Observa la data del usuario para actualizar la foto y bienvenida
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.txtWelcome.text = user.nombre
                verificarCargaCompleta()

                val imageUrl = if (!user.foto_perfil.isNullOrEmpty() && user.foto_perfil.startsWith("http", ignoreCase = true)) {
                    user.foto_perfil
                } else if (!user.foto_perfil.isNullOrEmpty()) {
                    "https://www.cuatroraices.cl/api/${user.foto_perfil}"
                } else {
                    null
                }

                if (imageUrl != null) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_user)
                        .into(binding.imgUserPhoto)
                } else {
                    binding.imgUserPhoto.setImageResource(R.drawable.ic_user)
                }
            }
            verificarCargaCompleta()
        }

        // Observa las estadísticas del Dashboard
        viewModel.dashboardData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.txtTotalPostulaciones.text = "${data.total_postulaciones} trabajos.-"
                verificarCargaCompleta()
                binding.txtUltimaPostulacion.text = data.ultima_postulacion?.let {
                    "${it.titulo} - ${it.empresa}"
                } ?: "Última: No hay postulaciones"

                val formatCLP = NumberFormat.getNumberInstance(Locale("es", "CL"))
                val formattedGanancia = "$" + formatCLP.format(data.total_ganado)
                binding.txtTotalGanado.text = formattedGanancia
            }
            verificarCargaCompleta()
        }
    }

    */
/**
     * Verifica si los datos han sido cargados correctamente y oculta el ProgressBar.
     *//*

    private fun verificarCargaCompleta() {
        val userDataLoaded = viewModel.userData.value != null
        val dashboardDataLoaded = viewModel.dashboardData.value != null


        if (userDataLoaded && dashboardDataLoaded) {
            binding.progressBar.visibility = View.GONE
            binding.contentContainer.visibility = View.VISIBLE
        }
    }

    private fun intentarCargarDatos() {
        // Obtén el RUT nuevamente desde SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val rutUsuario = sharedPreferences.getString("rut", null)

        if (rutUsuario.isNullOrEmpty()) {
            // Si aún es nulo, vuelve a intentarlo en 500ms
            binding.root.postDelayed({
                intentarCargarDatos()
            }, 500)
        } else {
            // Cuando el RUT esté disponible, carga los datos
            viewModel.loadUserData(rutUsuario)
            viewModel.loadDashboardData(rutUsuario)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
*/
