package com.likhachev.e_journal.presentation.ui.login

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
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentLoginBinding
import com.likhachev.e_journal.presentation.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        observeEvents()

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text?.toString()?.trim()
            val password = binding.passwordEditText.text?.toString()?.trim()

            if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(username, password)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    binding.loginButton.isEnabled = state !is LoginUiState.Loading
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is LoginUiEvent.ShowToast -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        }
                        is LoginUiEvent.NavigateToScreen -> {
                            when (event.roleId) {
                                1 -> findNavController().navigate(R.id.action_loginFragment_to_studentTabsFragment)
                                2 -> findNavController().navigate(R.id.action_loginFragment_to_teacherTabsFragment)
                                3 -> findNavController().navigate(R.id.action_loginFragment_to_adminTabsFragment)
                                else -> Unit
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}