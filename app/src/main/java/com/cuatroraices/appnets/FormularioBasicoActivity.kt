package com.cuatroraices.appnets

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FormularioBasicoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etRut: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etTelefono: EditText
    private lateinit var autoCompleteEtiquetas: AutoCompleteTextView
    private lateinit var chipGroupEtiquetas: ChipGroup
    private lateinit var btnGuardarPerfil: Button
    private lateinit var spinnerCiudades: Spinner
    private lateinit var autoCompleteCiudad: AutoCompleteTextView

    private val etiquetasSeleccionadas = mutableListOf<String>()
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_basico)

        // === Vinculación de vistas ===
        etNombre            = findViewById(R.id.etNombre)
        etRut               = findViewById(R.id.etRut)
        etDireccion         = findViewById(R.id.etDireccion)
        etTelefono          = findViewById(R.id.etTelefono)
        autoCompleteEtiquetas = findViewById(R.id.autoCompleteEtiquetas)
        chipGroupEtiquetas  = findViewById(R.id.chipGroupEtiquetas)
        btnGuardarPerfil    = findViewById(R.id.btnGuardarPerfil)
        spinnerCiudades     = findViewById(R.id.spinnerCiudades)
        autoCompleteCiudad  = findViewById(R.id.autoCompleteCiudad)

        // Recuperamos el email que viene del Intent
        email = intent.getStringExtra("email") ?: ""
        Log.d("FormularioBasico", "Email recibido: $email")

        // === Auto-formato y validación en tiempo real de RUT ===
        var isFormatting = false
        etRut.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val original = s?.toString() ?: ""
                // Limpiar todo excepto dígitos y 'K'
                val clean = original.replace("[^0-9kK]".toRegex(), "").uppercase(Locale.getDefault())

                val formatted = if (clean.length > 1) {
                    val body = clean.substring(0, clean.length - 1)
                    val dv = clean.last()
                    "$body-$dv"
                } else {
                    clean
                }

                etRut.setText(formatted)
                etRut.setSelection(formatted.length)

                // Validación en tiempo real
                if (formatted.contains("-") && !validarRut(formatted)) {
                    etRut.error = "RUT inválido"
                }

                isFormatting = false
            }
        })

        // === Carga datos iniciales ===
        obtenerEtiquetasDesdeAPI()
        obtenerCiudades()

        // Cuando seleccionan una etiqueta la agregamos como chip
        autoCompleteEtiquetas.setOnItemClickListener { parent, _, position, _ ->
            val etiqueta = parent.getItemAtPosition(position).toString()
            agregarEtiqueta(etiqueta)
            autoCompleteEtiquetas.text.clear()
        }

        btnGuardarPerfil.setOnClickListener {
            guardarPerfil()
        }
    }

    private fun obtenerEtiquetasDesdeAPI() {
        RetrofitInstance.api.obtenerEtiquetas().enqueue(object : Callback<EtiquetasResponse> {
            override fun onResponse(call: Call<EtiquetasResponse>, response: Response<EtiquetasResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val etiquetas = response.body()!!.etiquetas
                    val adapter = ArrayAdapter(
                        this@FormularioBasicoActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        etiquetas
                    )
                    autoCompleteEtiquetas.setAdapter(adapter)
                }
            }
            override fun onFailure(call: Call<EtiquetasResponse>, t: Throwable) {
                Toast.makeText(this@FormularioBasicoActivity, "Error al cargar etiquetas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerCiudades() {
        RetrofitInstance.api.obtenerCiudades().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful && response.body() != null) {
                    val adapter = ArrayAdapter(
                        this@FormularioBasicoActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        response.body()!!
                    )
                    autoCompleteCiudad.setAdapter(adapter)
                    autoCompleteCiudad.threshold = 1
                }
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@FormularioBasicoActivity, "Error al cargar ciudades", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun agregarEtiqueta(etiqueta: String) {
        if (!etiquetasSeleccionadas.contains(etiqueta)) {
            etiquetasSeleccionadas.add(etiqueta)
            val chip = Chip(this).apply {
                text = etiqueta
                isCloseIconVisible = true
                setTextAppearance(R.style.ChipTextStyle)
                setChipBackgroundColorResource(R.color.chipBackground)
                setTextColor(resources.getColor(R.color.white, theme))
                setOnCloseIconClickListener {
                    etiquetasSeleccionadas.remove(etiqueta)
                    chipGroupEtiquetas.removeView(this)
                }
            }
            chipGroupEtiquetas.addView(chip)
        }
    }

    private fun guardarPerfil() {
        val rutText        = etRut.text.toString().trim()
        val nombreText     = etNombre.text.toString().trim()
        val direccionText  = etDireccion.text.toString().trim()
        val telefonoText   = etTelefono.text.toString().trim()
        val etiquetasText  = etiquetasSeleccionadas.joinToString(",")
        val ciudadText     = autoCompleteCiudad.text.toString().trim()

        // === Validaciones ===
        if (rutText.isEmpty() || nombreText.isEmpty() ||
            direccionText.isEmpty() || telefonoText.isEmpty() ||
            etiquetasText.isEmpty() || ciudadText.isEmpty() || email.isEmpty()
        ) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            if (rutText.isEmpty())    etRut.error       = "RUT obligatorio"
            if (nombreText.isEmpty()) etNombre.error    = "Nombre obligatorio"
            return
        }
        if (!validarRut(rutText)) {
            etRut.error = "RUT inválido"
            Toast.makeText(this, "Ingresa un RUT válido", Toast.LENGTH_SHORT).show()
            return
        }

        // === Construir RequestBody para Multipart ===
        val rutPart       = rutText.toRequestBody("text/plain".toMediaTypeOrNull())
        val nombrePart    = nombreText.toRequestBody("text/plain".toMediaTypeOrNull())
        val direccionPart = direccionText.toRequestBody("text/plain".toMediaTypeOrNull())
        val telefonoPart  = telefonoText.toRequestBody("text/plain".toMediaTypeOrNull())
        val etiquetasPart = etiquetasText.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailPart     = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val ciudadPart    = ciudadText.toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitInstance.api.guardarPerfilConDocumentos(
            nombrePart,
            rutPart,
            direccionPart,
            telefonoPart,
            etiquetasPart,
            emailPart,
            ciudadPart,
            null,
            null
        ).enqueue(object : Callback<PerfilResponse> {
            override fun onResponse(call: Call<PerfilResponse>, response: Response<PerfilResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FormularioBasicoActivity, "Perfil guardado con éxito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@FormularioBasicoActivity, BienvenidaActivity::class.java).apply {
                        putExtra("USER_NAME", nombreText)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                } else {
                    Toast.makeText(this@FormularioBasicoActivity, "Error al guardar perfil", Toast.LENGTH_SHORT).show()
                    Log.e("FormularioBasico", "Error código ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<PerfilResponse>, t: Throwable) {
                Toast.makeText(this@FormularioBasicoActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** Valida RUT chileno (formato ########-K) */
    private fun validarRut(rut: String): Boolean {
        val clean = rut.replace(".", "").replace("-", "").uppercase(Locale.getDefault())
        if (clean.length < 2) return false
        val number = clean.dropLast(1).toIntOrNull() ?: return false
        val dv     = clean.last()
        val m      = arrayOf(2,3,4,5,6,7)
        var sum    = 0
        clean.dropLast(1).reversed().forEachIndexed { i, c ->
            sum += Character.getNumericValue(c) * m[i % m.size]
        }
        val res = 11 - (sum % 11)
        val dvCalc = when (res) {
            11 -> '0'
            10 -> 'K'
            else -> res.toString()[0]
        }
        return dv == dvCalc
    }
}

