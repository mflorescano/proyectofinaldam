package com.mario.citas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mario.citas.databinding.FragmentAdminBinding
import com.mario.citas.databinding.FragmentUserBinding
import com.mario.citas.ui.adapters.QuoteAdapter
import com.mario.citas.ui.viewmodels.AdminViewModel
import com.mario.citas.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private val argument by navArgs<UserFragmentArgs>()
    private val quoteAdapter by lazy { QuoteAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.success != null) {
                        viewModel.clear()
                        quoteAdapter.collection = it.success
                    }
                    if (it.error != null) {
                        Toast.makeText(requireContext(), "${it.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.rvQuotes.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.adapter = quoteAdapter
        }
        viewModel.getUserQuotes(argument.user.email)
        binding.tvName.text = "Nombre: ${argument.user.name}"
        binding.tvEmail.text = "Email: ${argument.user.email}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}