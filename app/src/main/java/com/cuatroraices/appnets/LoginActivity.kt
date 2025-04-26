package com.cuatroraices.appnets

import UsuarioResponse
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Button
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.ui.window.Dialog
import com.google.android.material.button.MaterialButton
import android.util.TypedValue



class LoginActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<View>(R.id.btnGoogleSignIn).setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w("LoginActivity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.id)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "signInWithCredential:success")
                    val email = acct.email ?: ""
                    verificarPerfil(email)
                } else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun verificarPerfil(email: String) {
        Log.d("LoginActivity", "Email enviado a CrearPerfilActivity: $email")

        // Usa tu singleton de Retrofit
        val api = RetrofitInstance.api

        // Llamada asíncrona con enqueue
        api.verificarPerfil(email).enqueue(object : Callback<PerfilResponse> {
            override fun onResponse(call: Call<PerfilResponse>, response: Response<PerfilResponse>) {
                if (response.isSuccessful && response.body()?.tienePerfil == true) {
                    // Ir a MainActivity si hay perfil
                    obtenerYGuardarPerfil(email)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Mostrar alerta de bienvenida si no hay perfil
                    mostrarAlertaBienvenida(email)

                }
            }

            override fun onFailure(call: Call<PerfilResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error al verificar perfil", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Fallo en verificarPerfil()", t)
            }
        })
    }

    private fun obtenerYGuardarPerfil(email: String) {
        val api = RetrofitInstance.api

        api.obtenerPerfil(email).enqueue(object : Callback<UsuarioResponse> {
            override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("API_RESPONSE", "Respuesta completa: $body") // 🚀 Imprime toda la respuesta recibida

                    val usuario = body?.usuario
                    if (usuario != null) {
                        Log.d("API_RESPONSE", "Usuario obtenido: Nombre=${usuario.nombre}, RUT=${usuario.rut}, Email=${usuario.email}")

                        // Guardar en SharedPreferences
                        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("nombre", usuario.nombre)
                            putString("rut", usuario.rut)
                            putString("direccion", usuario.direccion)
                            putString("telefono", usuario.telefono)
                            putString("email", usuario.email)
                            apply()
                        }
                        Log.d("API_RESPONSE", "✅ Datos guardados en SharedPreferences correctamente")
                    } else {
                        Log.e("API_RESPONSE", "⚠ No se encontraron datos del usuario en la respuesta")
                    }
                } else {
                    // Registra el código de error y el cuerpo de la respuesta
                    val errorBody = response.errorBody()?.string() ?: "Cuerpo de error vacío"
                    Log.e("API_RESPONSE", "❌ Error en la respuesta: Código=${response.code()}, Mensaje=${response.message()}, Detalles=$errorBody")
                }
            }

            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                Log.e("API_RESPONSE", "🚨 Error en la conexión: ${t.message}")
            }
        })
    }


    private fun mostrarAlertaBienvenida(email: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_bienvenida, null)
        dialog.setContentView(view)

        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 340f, resources.displayMetrics).toInt()
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600f, resources.displayMetrics).toInt()
        dialog.window?.setLayout(width, height)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.alpha = 0f
        view.translationY = 150f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        val btnCompletarPerfil = view.findViewById<MaterialButton>(R.id.btnCompletarPerfil)
        btnCompletarPerfil.setOnClickListener {
            dialog.dismiss()

            // ✅ **Ahora usamos `email` correctamente al enviarlo a CrearPerfilActivity**
            val intent = Intent(this, FormularioBasicoActivity::class.java)
            intent.putExtra("email", email)  // <-- **Aquí enviamos el email**
            startActivity(intent)
        }

        dialog.show()
    }

}
