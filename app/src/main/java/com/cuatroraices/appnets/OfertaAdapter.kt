package com.cuatroraices.appnets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.chromium.base.Log

class OfertaAdapter(
    private val listaOfertas: List<Oferta>,
    private val postulaciones: List<Int> // Lista de IDs de ofertas ya postuladas
) : RecyclerView.Adapter<OfertaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val txtDescripcion: TextView = view.findViewById(R.id.txtDescripcion)
        val txtUbicacion: TextView = view.findViewById(R.id.txtUbicacion)
        val txtEmpresa: TextView = view.findViewById(R.id.tvEmpresa)
        val txtRemuneracion: TextView = view.findViewById(R.id.tvRemuneracion)
        val txtModalidad: TextView = view.findViewById(R.id.txtModalidad)
        val tvPostulado: TextView = view.findViewById(R.id.tvPostulado) // Cinta de "Postulado"
        val ivCheck: ImageView = view.findViewById(R.id.ivCheck) // Icono de check
        val tvDestacada: TextView = view.findViewById(R.id.tvDestacada)
        val cardOferta: androidx.cardview.widget.CardView = view.findViewById(R.id.cardOferta)// 🔥 Nueva etiqueta para destacadas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_oferta, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oferta = listaOfertas[position]

        // Asignar datos
        holder.txtTitulo.text = oferta.titulo ?: "Sin título"
        holder.txtDescripcion.text = oferta.descripcion ?: "Sin descripción"
        holder.txtEmpresa.text = "Empresa: ${oferta.nombre_empresa ?: "Desconocida"}"
        holder.txtRemuneracion.text = "Sueldo: ${oferta.remuneracion ?: "0"} CLP"
        holder.txtUbicacion.text = "Ubicación: ${oferta.ubicacion ?: "No especificada"}"
        holder.txtModalidad.text = "Modalidad: ${oferta.modalidad ?: "No especificada"}"

        // **Marcar las ofertas destacadas con color diferente**
        if (oferta.categoria == "destacada" && position < 10) {
            holder.tvDestacada.visibility = View.VISIBLE
            holder.cardOferta.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorDestacada))
        } else {
            holder.tvDestacada.visibility = View.GONE
            holder.cardOferta.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.blanco))
        }

        // **Comprobar si el usuario ya postuló**
        if (postulaciones.contains(oferta.id)) {
            holder.tvPostulado.visibility = View.VISIBLE
            holder.ivCheck.visibility = View.VISIBLE
        } else {
            holder.tvPostulado.visibility = View.GONE
            holder.ivCheck.visibility = View.GONE
        }

        // **Abrir BottomSheet al tocar la oferta**
        holder.itemView.setOnClickListener {
            val fragmentManager = (holder.itemView.context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.let {
                PostulacionBottomSheet(oferta).show(it, "PostulacionBottomSheet")
            }
        }
    }


    /*override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oferta = listaOfertas[position]

        // 🔹 LOGS PARA DEPURAR
        Log.d("OfertaAdapter", "Oferta ID: ${oferta.id}, Empresa: ${oferta.nombre_empresa}, Sueldo: ${oferta.remuneracion}")

        holder.txtTitulo.text = oferta.titulo ?: "Sin título"
        holder.txtDescripcion.text = oferta.descripcion ?: "Sin descripción"
        holder.txtEmpresa.text = "Empresa: ${oferta.nombre_empresa ?: "Desconocida"}"
        holder.txtRemuneracion.text = "Sueldo: ${oferta.remuneracion ?: "0"} CLP"
        holder.txtUbicacion.text = "Ubicación: ${oferta.ubicacion ?: "No especificada"}"
        holder.txtModalidad.text = "Modalidad: ${oferta.modalidad ?: "No especificada"}"

        // **Comprobar si la oferta ya fue postulada**
        if (postulaciones.contains(oferta.id)) {
            holder.tvPostulado.visibility = View.VISIBLE  // Muestra la cinta "Postulado"
            holder.ivCheck.visibility = View.VISIBLE      // Muestra el icono de check
        } else {
            holder.tvPostulado.visibility = View.GONE
            holder.ivCheck.visibility = View.GONE
        }

        // **Nueva validación: Agregar etiqueta para destacadas en el Top 10**
        if (oferta.categoria == "destacada" && position < 10) {
            holder.tvDestacada.visibility = View.VISIBLE
            holder.tvDestacada.text = "🔥 Destacada"
        } else {
            holder.tvDestacada.visibility = View.GONE
        }

        // **Abrir el BottomSheet al tocar la oferta**
        holder.itemView.setOnClickListener {
            val fragmentManager = (holder.itemView.context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.let {
                PostulacionBottomSheet(oferta).show(it, "PostulacionBottomSheet")
            }
        }
    }*/

    override fun getItemCount(): Int = listaOfertas.size
}


/*package com.cuatroraices.appnets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import org.chromium.base.Log

class OfertaAdapter(
    private val listaOfertas: List<Oferta>,
    private val postulaciones: List<Int> // Lista de IDs de ofertas ya postuladas
) : RecyclerView.Adapter<OfertaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val txtDescripcion: TextView = view.findViewById(R.id.txtDescripcion)
        val txtUbicacion: TextView = view.findViewById(R.id.txtUbicacion)
        val txtEmpresa: TextView = view.findViewById(R.id.tvEmpresa)
        val txtRemuneracion: TextView = view.findViewById(R.id.tvRemuneracion)
        val txtModalidad: TextView = view.findViewById(R.id.txtModalidad)
        val tvPostulado: TextView = view.findViewById(R.id.tvPostulado) // Cinta
        val ivCheck: ImageView = view.findViewById(R.id.ivCheck) // Icono de check
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_oferta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oferta = listaOfertas[position]

        // 🔹 LOGS PARA DEPURAR
        Log.d("OfertaAdapter", "Oferta ID: ${oferta.id}")
        Log.d("OfertaAdapter", "Empresa: ${oferta.nombre_empresa}")
        Log.d("OfertaAdapter", "Sueldo: ${oferta.remuneracion}")
        Log.d("OfertaAdapter", "Descripción corta: ${oferta.descripcion}")
        Log.d("OfertaAdapter", "Descripción larga: ${oferta.descripcion_larga}")

        holder.txtTitulo.text = oferta.titulo
        holder.txtDescripcion.text = oferta.descripcion
        holder.txtEmpresa.text = "Empresa: ${oferta.nombre_empresa}"
        holder.txtRemuneracion.text = "Sueldo: ${oferta.remuneracion} CLP"
        holder.txtUbicacion.text = "Ubicación: ${oferta.ubicacion}"
        holder.txtModalidad.text = "Modalidad: ${oferta.modalidad}"

        // **Comprobar si la oferta ya fue postulada**
        if (postulaciones.contains(oferta.id)) {
            holder.tvPostulado.visibility = View.VISIBLE  // Muestra la cinta "Postulado"
            holder.ivCheck.visibility = View.VISIBLE      // Muestra el icono de check
        } else {
            holder.tvPostulado.visibility = View.GONE
            holder.ivCheck.visibility = View.GONE
        }

        // **Abrir el BottomSheet al tocar la oferta**
        holder.itemView.setOnClickListener {
            val fragmentManager = (holder.itemView.context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.let {
                PostulacionBottomSheet(oferta).show(it, "PostulacionBottomSheet")
            }
        }
    }

    override fun getItemCount(): Int = listaOfertas.size
}*/
