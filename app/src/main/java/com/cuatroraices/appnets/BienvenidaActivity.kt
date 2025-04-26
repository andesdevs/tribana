package com.cuatroraices.appnets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class BienvenidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        // 1) Logo (opcional si quieres hacer algo dinámico)
        val logo = findViewById<ImageView>(R.id.ivSplashLogo)
        // logo.setImageResource(...) // si lo cambias dinámicamente

        // 2) Saludo personalizado
        val userName = intent.getStringExtra("USER_NAME") ?: "amiga"
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = "¡Hola, $userName! 👋"

        Log.d("BienvenidaActivity", "Mostrando bienvenida para $userName")

        // 3) Layout Animation en el LinearLayout interno
        val llBienvenida = findViewById<LinearLayout>(R.id.llBienvenida)
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down)
        llBienvenida.layoutAnimation = controller
        llBienvenida.scheduleLayoutAnimation()

        // 4) Botón Continuar
        findViewById<MaterialButton>(R.id.btnContinuar).setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
        }
    }
}
