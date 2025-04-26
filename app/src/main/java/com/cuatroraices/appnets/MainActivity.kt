package com.cuatroraices.appnets

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import ir.programmerplus.curvenavx.BottomNavigation

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1️⃣ NavController
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHost.navController

        // 2️⃣ CurveNavX BottomNavigation
        val bottomNav = findViewById<BottomNavigation>(R.id.bottom_navigation)

        // 3️⃣ Listener: ‘item.id’ **es** el índice de la celda (0-based)
        bottomNav.setOnShowListener { item ->
            val index = item.id
            Log.d(TAG, "TAB PULSADO → índice=$index")

            // Opciones comunes de navegación
            val opts = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.graph.startDestinationId, false)
                .build()

            // Mapea el índice a tu acción global
            val actionId = when (index) {
                0 -> R.id.action_global_navigation_home
                1 -> R.id.action_global_navigation_dashboard
                2 -> R.id.action_global_navigation_notifications
                3 -> R.id.action_global_navigation_red_de_apoyo
                else -> {
                    Log.e(TAG, "Índice inesperado: $index")
                    return@setOnShowListener
                }
            }
            Log.d(TAG, "→ Navegando a actionId=$actionId")

            navController.navigate(actionId, null, opts)
        }

        // 4️⃣ Muestra por defecto tu Dashboard (el segundo tab, índice=1)
        bottomNav.show(R.id.navigation_dashboard)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        return navHost.navController.navigateUp()
    }
}
