package com.likhachev.e_journal.presentation.ui.admin_groups

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
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentAdminGroupsBinding
import com.likhachev.e_journal.presentation.viewmodel.AdminGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminGroupsFragment : Fragment() {

    private var _binding: FragmentAdminGroupsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminGroupsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val groupName = binding.groupNameEditText.text.toString().trim()
            val startYearText = binding.startYearEditText.text.toString().trim()

            if (startYearText.isNotEmpty()) {
                try {
                    val startYear = startYearText.toInt()
                    viewModel.createGroup(groupName, startYear)
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Введите корректный год", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AdminGroupsUiState.Idle -> {
                            hideLoading()
                        }
                        is AdminGroupsUiState.Loading -> {
                            showLoading()
                        }
                        is AdminGroupsUiState.Success -> {
                            hideLoading()
                            clearFields()
                        }
                        is AdminGroupsUiState.Error -> {
                            hideLoading()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.successEvent.collect { event ->
                    event.getContentIfNotHandled()?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorEvent.collect { event ->
                    event.getContentIfNotHandled()?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.saveButton.isEnabled = false
        binding.saveButton.text = ""
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.saveButton.isEnabled = true
        binding.saveButton.text = getString(R.string.save_button_text)
        binding.progressBar.visibility = View.GONE
    }

    private fun clearFields() {
        binding.groupNameEditText.text?.clear()
        binding.startYearEditText.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}