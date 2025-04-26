package com.cuatroraices.appnets.ui.redapoyo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cuatroraices.appnets.databinding.FragmentRedDeApoyoBinding

class RedApoyoFragment : Fragment() {

    private var _binding: FragmentRedDeApoyoBinding? = null
    private val binding get() = _binding!!

    // Obtiene el ViewModel asociado a este fragment
    private val viewModel: RedApoyoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRedDeApoyoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observa la lista de recursos y actualiza la UI cuando cambie
        viewModel.recursos.observe(viewLifecycleOwner, Observer { lista ->
            // Por ahora, mostramos en un TextView; aquí podrías usar un RecyclerView
            binding.tvContenido.text = lista.joinToString("\n\n") { "• $it" }
        })

        // Carga inicial de recursos
        viewModel.cargarRecursos()

        // Si quisieras más lógica (botones, etc.) la agregas aquí...
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
