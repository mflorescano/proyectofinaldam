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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mario.citas.core.utils.initPiker
import com.mario.citas.core.utils.showTimePiker
import com.mario.citas.core.utils.transformIntoDatePicker
import com.mario.citas.databinding.FragmentQuoteBinding
import com.mario.citas.ui.models.CalendarEntity
import com.mario.citas.ui.models.QuoteEntity
import com.mario.citas.ui.viewmodels.QuoteViewModel
import java.util.Date
import kotlinx.coroutines.launch

class QuoteFragment : Fragment() {

    private var _binding: FragmentQuoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuoteViewModel by viewModels()
    private val arguments by navArgs<QuoteFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.success != null) {
                        viewModel.clear()
                        Toast.makeText(requireContext(), "Cita creada!", Toast.LENGTH_SHORT).show()
                    }
                    if (it.error != null) {
                        Toast.makeText(requireContext(), "${it.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListeners()
    }

    private fun initView() {
        binding.etDate.transformIntoDatePicker(requireContext(), "MM-dd-yyyy", Date())
        binding.etTime.initPiker()
        binding.etTimeEnd.initPiker()
    }

    private fun initListeners() {

        binding.etTime.setOnClickListener {
            binding.etTime.showTimePiker(requireActivity().supportFragmentManager)
        }

        binding.etTimeEnd.setOnClickListener {
            binding.etTimeEnd.showTimePiker(requireActivity().supportFragmentManager)
        }

        binding.btnLogin.setOnClickListener {
            viewModel.registerQuote(
                QuoteEntity(
                    inNameOf = arguments.user.name,
                    emailOf = arguments.user.email,
                    quoteStart = CalendarEntity(
                        day = getDay(binding.etDate.text.toString()),
                        month = getMonth(binding.etDate.text.toString()),
                        year = getYear(binding.etDate.text.toString()),
                        hour = getHour(binding.etTime.text.toString()),
                        minute = getMinute(binding.etTime.text.toString())
                    ),
                    quoteEnd = CalendarEntity(
                        day = getDay(binding.etDate.text.toString()),
                        month = getMonth(binding.etDate.text.toString()),
                        year = getYear(binding.etDate.text.toString()),
                        hour = getHour(binding.etTimeEnd.text.toString()),
                        minute = getMinute(binding.etTimeEnd.text.toString())
                    )
                )
            )
        }

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(
                QuoteFragmentDirections.actionQuoteFragmentToUserFragment(
                    arguments.user
                )
            )
        }
    }

    private fun getDay(time: String): Int {
        val split = time.split("-")
        return split.first().toInt()
    }

    private fun getMonth(time: String): Int {
        val split = time.split("-")
        return split[1].toInt()
    }

    private fun getYear(time: String): Int {
        val split = time.split("-")
        return split.last().toInt()
    }

    private fun getHour(time: String): Int {
        val split = time.split(":")
        return split.first().toInt()
    }

    private fun getMinute(time: String): Int {
        val split = time.split(":")
        return split.last().toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}