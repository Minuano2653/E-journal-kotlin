package com.likhachev.e_journal.presentation.ui.admin_students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentAdminStudentsBinding
import com.likhachev.e_journal.presentation.viewmodel.AdminStudentsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminStudentsFragment : Fragment() {

    private var _binding: FragmentAdminStudentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminStudentsViewModel by viewModels()

    private var selectedGroupId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminStudentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
        setupGroupSelection()
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadGroups()
        }

        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
        )
    }


    private fun setupGroupSelection() {
        binding.groupEditText.isFocusable = false
        binding.groupEditText.isClickable = true

        binding.groupEditTextLayout.setOnClickListener {
            showGroupSelectionDialog()
        }

        binding.groupEditText.setOnClickListener {
            showGroupSelectionDialog()
        }
    }

    private fun showGroupSelectionDialog() {
        val groups = viewModel.groups
        if (groups.isEmpty()) {
            Toast.makeText(requireContext(), "Группы не загружены", Toast.LENGTH_SHORT).show()
            return
        }

        val groupNames = groups.map { "${it.name} (${it.startYear}-${it.endYear})" }.toTypedArray()

        // Найдем индекс текущей выбранной группы
        val currentSelection = if (selectedGroupId.isNotEmpty()) {
            groups.indexOfFirst { it.id == selectedGroupId }
        } else {
            -1
        }

        AlertDialog.Builder(requireContext(), R.style.CustomDatePickerDialogTheme)
            .setTitle("Выберите группу")
            .setSingleChoiceItems(groupNames, currentSelection) { dialog, which ->
                val selectedGroup = groups[which]
                selectedGroupId = selectedGroup.id
                binding.groupEditText.setText(selectedGroup.name)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val lastName = binding.lastNameInput.text.toString().trim()
            val firstName = binding.firstNameInput.text.toString().trim()
            val middleName = binding.middleNameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            viewModel.createStudent(lastName, firstName, middleName, email, password, selectedGroupId)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AdminStudentsUiState.Idle -> {
                            hideLoading()
                        }
                        is AdminStudentsUiState.Loading -> {
                            showLoading()
                        }
                        is AdminStudentsUiState.CreatingStudent -> {
                            showCreatingStudentLoading()
                        }
                        is AdminStudentsUiState.GroupsLoaded -> {
                            hideLoading()
                            binding.swipeRefreshLayout.isRefreshing = false

                        }
                        is AdminStudentsUiState.Success -> {
                            hideLoading()
                            clearFields()
                            binding.swipeRefreshLayout.isRefreshing = false

                        }
                        is AdminStudentsUiState.Error -> {
                            hideLoading()
                            binding.swipeRefreshLayout.isRefreshing = false

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
        binding.groupEditTextLayout.isEnabled = false
        binding.saveButton.isEnabled = false
    }

    private fun showCreatingStudentLoading() {
        binding.saveButton.isEnabled = false
        binding.saveButton.text = ""
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.groupEditTextLayout.isEnabled = true
        binding.saveButton.isEnabled = true
        binding.saveButton.text = getString(R.string.save_button_text)
        binding.progressBar.visibility = View.GONE
    }

    private fun clearFields() {
        binding.lastNameInput.text?.clear()
        binding.firstNameInput.text?.clear()
        binding.middleNameInput.text?.clear()
        binding.emailInput.text?.clear()
        binding.passwordInput.text?.clear()
        binding.groupEditText.setText("")
        selectedGroupId = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}