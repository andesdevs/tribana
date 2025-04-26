package com.cuatroraices.appnets.ui.notifications

import UsuarioResponse
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cuatroraices.appnets.R
import com.cuatroraices.appnets.RetrofitInstance
import com.cuatroraices.appnets.UpdateUsuarioResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.Chip
import com.cuatroraices.appnets.EtiquetasResponse




private const val BASE_UPLOADS_URL = "https://www.cuatroraices.cl/api/uploads/"


class NotificationsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etNombre: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCiudad: EditText
    private lateinit var imgCedula: ImageView
    private lateinit var imgCuidadora: ImageView
    private lateinit var chipGroupEtiquetas: ChipGroup
    private lateinit var btnSubirCedula: Button
    private lateinit var btnSubirCuidadora: Button
    private lateinit var btnGuardarCambios: Button
    private lateinit var etEtiquetas: AutoCompleteTextView
    private val etiquetasSeleccionadas = mutableListOf<String>()






    private var rutUsuario: String = ""
    private var carnetIdentidadFile: File? = null
    private var carnetCuidadoraFile: File? = null

    companion object {
        private const val REQUEST_IMAGE_CEDULA = 1
        private const val REQUEST_IMAGE_CUIDADORA = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Referencias a los elementos del layout
        etNombre = root.findViewById(R.id.etNombre)
        etDireccion = root.findViewById(R.id.etDireccion)
        etTelefono = root.findViewById(R.id.etTelefono)
        etCiudad = root.findViewById(R.id.etCiudad)
        etEtiquetas = root.findViewById(R.id.etEtiquetas) as AutoCompleteTextView
        imgCedula = root.findViewById(R.id.imgCedula)
        imgCuidadora = root.findViewById(R.id.imgCuidadora)
        btnSubirCedula = root.findViewById(R.id.btnSubirCedula)
        btnSubirCuidadora = root.findViewById(R.id.btnSubirCuidadora)
        btnGuardarCambios = root.findViewById(R.id.btnGuardarCambios)
        chipGroupEtiquetas = root.findViewById(R.id.chipGroupEtiquetas)


        // Recuperar el RUT desde SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Activity.MODE_PRIVATE)
        rutUsuario = sharedPreferences.getString("rut", "") ?: ""
        Log.d("PerfilFragment", "RUT obtenido: $rutUsuario")

        // Obtener datos del usuario
        obtenerDatosUsuario()
        obtenerEtiquetasDesdeAPI()


        // Eventos de los botones
        btnSubirCedula.setOnClickListener { abrirCamara(REQUEST_IMAGE_CEDULA) }
        btnSubirCuidadora.setOnClickListener { abrirCamara(REQUEST_IMAGE_CUIDADORA) }
        btnGuardarCambios.setOnClickListener { actualizarPerfil() }

        return root
    }

    private fun obtenerEtiquetasDesdeAPI() {
        val apiService = RetrofitInstance.api
        apiService.obtenerEtiquetas().enqueue(object : Callback<EtiquetasResponse> {
            override fun onResponse(call: Call<EtiquetasResponse>, response: Response<EtiquetasResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val etiquetas = response.body()!!.etiquetas
                    Log.d("Etiquetas", "Etiquetas disponibles: $etiquetas")

                    // Configurar el autocompletado
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, etiquetas)
                    etEtiquetas.setAdapter(adapter)

                    // Cuando el usuario selecciona una etiqueta, agregarla como chip
                    etEtiquetas.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                        val etiquetaSeleccionada = parent.getItemAtPosition(position).toString()
                        agregarEtiqueta(etiquetaSeleccionada)
                        etEtiquetas.text.clear()
                    }
                } else {
                    Log.e("Etiquetas", "Error en la API: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<EtiquetasResponse>, t: Throwable) {
                Log.e("Etiquetas", "Error de conexión: ${t.message}")
            }
        })
    }


    private fun obtenerDatosUsuario() {
        if (rutUsuario.isEmpty()) {
            Log.e("PerfilFragment", "No se encontró el RUT del usuario en SharedPreferences")
            return
        }


        Log.d("PerfilFragment", "Obteniendo perfil para el RUT: $rutUsuario")

        val apiService = RetrofitInstance.api
        apiService.obtenerPerfilPorRut(rutUsuario).enqueue(object : Callback<UsuarioResponse> {
            override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {

                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()?.usuario

                    if (usuario != null) {
                        Log.d("PerfilFragment", "Usuario recibido: $usuario")
                        etNombre.setText(usuario.nombre ?: "")
                        etDireccion.setText(usuario.direccion ?: "")
                        etTelefono.setText(usuario.telefono ?: "")
                        etCiudad.setText(usuario.ciudad ?: "")
                        usuario.etiquetas?.split(",")?.map { it.trim() }?.forEach { etiqueta ->
                            agregarEtiqueta(etiqueta)
                        }

                        // Verificar y construir URL del carnet de identidad
                        val carnetIdentidadUrl = if (!usuario.carnet_identidad.isNullOrEmpty() &&
                            !usuario.carnet_identidad.startsWith("http", ignoreCase = true)) {
                            "https://www.cuatroraices.cl/api/${usuario.carnet_identidad}"
                        } else usuario.carnet_identidad ?: ""

                        if (carnetIdentidadUrl.isNotEmpty()) {
                            Log.d("PerfilFragment", "Cargando imagen de cédula: $carnetIdentidadUrl")
                            Glide.with(requireContext())
                                .load(carnetIdentidadUrl)
                                .placeholder(R.drawable.ic_user_placeholder) // Placeholder si tarda en cargar
                                .into(imgCedula)
                        } else {
                            Log.e("PerfilFragment", "No se encontró carnet de identidad")
                        }

                        // Verificar y construir URL del carnet de cuidadora
                        val carnetCuidadoraUrl = if (!usuario.carnet_cuidadora.isNullOrEmpty() &&
                            !usuario.carnet_cuidadora.startsWith("http", ignoreCase = true)) {
                            "https://www.cuatroraices.cl/api/${usuario.carnet_cuidadora}"
                        } else usuario.carnet_cuidadora ?: ""

                        if (carnetCuidadoraUrl.isNotEmpty()) {
                            Log.d("PerfilFragment", "Cargando imagen de cuidadora: $carnetCuidadoraUrl")
                            Glide.with(requireContext())
                                .load(carnetCuidadoraUrl)
                                .placeholder(R.drawable.ic_user_placeholder)
                                .into(imgCuidadora)
                        } else {
                            Log.e("PerfilFragment", "No se encontró carnet de cuidadora")
                        }
                    } else {
                        Log.e("PerfilFragment", "Perfil de usuario no disponible en la respuesta")
                        Toast.makeText(requireContext(), "Perfil de usuario no disponible", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("PerfilFragment", "Error en respuesta: Código ${response.code()}, Body: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "No se pudo obtener el perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {

                Log.e("PerfilFragment", "Error de conexión: ${t.message}", t)
                Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun abrirCamara(requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, requestCode)
        }
    }

    private fun agregarEtiqueta(etiqueta: String) {
        // Evita duplicados
        if (!etiquetasSeleccionadas.contains(etiqueta)) {
            etiquetasSeleccionadas.add(etiqueta)

            // Crea el Chip
            val chip = Chip(requireContext()).apply {
                text = etiqueta
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    etiquetasSeleccionadas.remove(etiqueta)
                    chipGroupEtiquetas.removeView(this)
                }
            }
            // Agrega el Chip al ChipGroup
            chipGroupEtiquetas.addView(chip)
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            val fileName = when (requestCode) {
                REQUEST_IMAGE_CEDULA -> "CI-$rutUsuario.jpg"
                REQUEST_IMAGE_CUIDADORA -> "CUIDADORA-$rutUsuario.jpg"
                else -> null
            }
            if (fileName != null) {
                val savedFile = guardarImagenEnArchivo(imageBitmap, fileName)
                if (requestCode == REQUEST_IMAGE_CEDULA) {
                    carnetIdentidadFile = savedFile
                    imgCedula.setImageBitmap(imageBitmap)
                } else {
                    carnetCuidadoraFile = savedFile
                    imgCuidadora.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    private fun guardarImagenEnArchivo(bitmap: Bitmap, nombreArchivo: String): File? {
        val directorio = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File(directorio, nombreArchivo)

        return try {
            val fos = FileOutputStream(archivo)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            archivo
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun actualizarPerfil() {
        val nombre = etNombre.text.toString()
        val direccion = etDireccion.text.toString()
        val telefono = etTelefono.text.toString()
        val ciudad = etCiudad.text.toString()
        val etiquetas = etiquetasSeleccionadas.joinToString(",")

        Log.d("PerfilFragment", "Datos antes de enviar:")
        Log.d("PerfilFragment", "Nombre: $nombre, Dirección: $direccion, Teléfono: $telefono, Ciudad: $ciudad, Etiquetas: $etiquetas")
        Log.d("PerfilFragment", "Rut: $rutUsuario")

        val rutPart = RequestBody.create("text/plain".toMediaTypeOrNull(), rutUsuario)
        val nombrePart = RequestBody.create("text/plain".toMediaTypeOrNull(), nombre)
        val direccionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), direccion)
        val telefonoPart = RequestBody.create("text/plain".toMediaTypeOrNull(), telefono)
        val ciudadPart = RequestBody.create("text/plain".toMediaTypeOrNull(), ciudad)
        val etiquetasPart = RequestBody.create("text/plain".toMediaTypeOrNull(), etiquetas)

        val carnetIdentidadPart = carnetIdentidadFile?.let {
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), it)
            MultipartBody.Part.createFormData("carnet_identidad", it.name, requestFile)
        }

        val carnetCuidadoraPart = carnetCuidadoraFile?.let {
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), it)
            MultipartBody.Part.createFormData("carnet_cuidadora", it.name, requestFile)
        }

        val apiService = RetrofitInstance.api
        apiService.actualizarPerfil(
            rutPart, nombrePart, direccionPart, telefonoPart, ciudadPart, etiquetasPart,
            carnetIdentidadPart, carnetCuidadoraPart
        ).enqueue(object : Callback<UpdateUsuarioResponse> {
            override fun onResponse(call: Call<UpdateUsuarioResponse>, response: Response<UpdateUsuarioResponse>) {
                if (response.isSuccessful) {
                    Log.d("PerfilFragment", "Respuesta exitosa: ${response.body()}")
                    Toast.makeText(requireContext(), "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("PerfilFragment", "Error en respuesta: Código ${response.code()}, Body: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateUsuarioResponse>, t: Throwable) {
                Log.e("PerfilFragment", "Fallo en la conexión: ${t.message}", t)
                Toast.makeText(requireContext(), "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

