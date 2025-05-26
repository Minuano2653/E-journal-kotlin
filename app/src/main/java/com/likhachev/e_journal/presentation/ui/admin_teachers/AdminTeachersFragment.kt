package com.likhachev.e_journal.presentation.ui.admin_teachers

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
import com.likhachev.e_journal.databinding.FragmentAdminTeachersBinding
import com.likhachev.e_journal.presentation.viewmodel.AdminTeachersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminTeachersFragment : Fragment() {

    private var _binding: FragmentAdminTeachersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminTeachersViewModel by viewModels()

    private var selectedSubjectId: Int = -1
    private var selectedGroupIds: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminTeachersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
        setupSelectionFields()
    }

    private fun setupSelectionFields() {
        // Предмет - single choice
        binding.subjectsEditText.isFocusable = false
        binding.subjectsEditText.isClickable = true/*
        binding.subjectsEditTextLayout.setOnClickListener {
            showSubjectSelectionDialog()
        }*/
        binding.subjectsEditText.setOnClickListener {
            showSubjectSelectionDialog()
        }

        // Группы - multi choice
        binding.groupEditText.isFocusable = false
        binding.groupEditText.isClickable = true
        /*binding.groupEditTextLayout.setOnClickListener {
            showGroupSelectionDialog()
        }*/
        binding.groupEditText.setOnClickListener {
            showGroupSelectionDialog()
        }
    }

    private fun showSubjectSelectionDialog() {
        val subjects = viewModel.subjects
        if (subjects.isEmpty()) {
            Toast.makeText(requireContext(), "Предметы не загружены", Toast.LENGTH_SHORT).show()
            return
        }

        val subjectNames = subjects.map { it.name }.toTypedArray()
        val currentSelection = if (selectedSubjectId != -1) {
            subjects.indexOfFirst { it.id == selectedSubjectId }
        } else {
            -1
        }

        AlertDialog.Builder(requireContext(), R.style.CustomDatePickerDialogTheme)
            .setTitle("Выберите предмет")
            .setSingleChoiceItems(subjectNames, currentSelection) { dialog, which ->
                val selectedSubject = subjects[which]
                selectedSubjectId = selectedSubject.id
                binding.subjectsEditText.setText(selectedSubject.name)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showGroupSelectionDialog() {
        val groups = viewModel.groups
        if (groups.isEmpty()) {
            Toast.makeText(requireContext(), "Группы не загружены", Toast.LENGTH_SHORT).show()
            return
        }

        val groupNames = groups.map { "${it.name} (${it.startYear}-${it.endYear})" }.toTypedArray()
        val checkedItems = BooleanArray(groups.size) { index ->
            selectedGroupIds.contains(groups[index].id)
        }

        AlertDialog.Builder(requireContext(), R.style.CustomDatePickerDialogTheme)
            .setTitle("Выберите группы")
            .setMultiChoiceItems(groupNames, checkedItems) { _, which, isChecked ->
                val groupId = groups[which].id
                if (isChecked) {
                    if (!selectedGroupIds.contains(groupId)) {
                        selectedGroupIds.add(groupId)
                    }
                } else {
                    selectedGroupIds.remove(groupId)
                }
            }
            .setPositiveButton("OK") { dialog, _ ->
                updateGroupsText()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateGroupsText() {
        if (selectedGroupIds.isEmpty()) {
            binding.groupEditText.setText("")
            return
        }

        val selectedGroupNames = viewModel.groups
            .filter { selectedGroupIds.contains(it.id) }
            .map { it.name }

        val displayText = when {
            selectedGroupNames.size == 1 -> selectedGroupNames.first()
            selectedGroupNames.size <= 3 -> selectedGroupNames.joinToString(", ")
            else -> "${selectedGroupNames.take(2).joinToString(", ")} и ещё ${selectedGroupNames.size - 2}"
        }

        binding.groupEditText.setText(displayText)
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val lastName = binding.lastNameEditText.text.toString().trim()
            val firstName = binding.firstNameEditText.text.toString().trim()
            val middleName = binding.middleNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            viewModel.createTeacher(
                lastName, firstName, middleName, email, password,
                selectedGroupIds, selectedSubjectId
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AdminTeachersUiState.Idle -> {
                            hideLoading()
                        }
                        is AdminTeachersUiState.Loading -> {
                            showLoading()
                        }
                        is AdminTeachersUiState.CreatingTeacher -> {
                            showCreatingTeacherLoading()
                        }
                        is AdminTeachersUiState.DataLoaded -> {
                            hideLoading()
                        }
                        is AdminTeachersUiState.Success -> {
                            hideLoading()
                            clearFields()
                        }
                        is AdminTeachersUiState.Error -> {
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
        binding.subjectsEditTextLayout.isEnabled = false
        binding.groupEditTextLayout.isEnabled = false
        binding.saveButton.isEnabled = false
    }

    private fun showCreatingTeacherLoading() {
        binding.saveButton.isEnabled = false
        binding.saveButton.text = ""
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.subjectsEditTextLayout.isEnabled = true
        binding.groupEditTextLayout.isEnabled = true
        binding.saveButton.isEnabled = true
        binding.saveButton.text = getString(R.string.save_button_text)
        binding.progressBar.visibility = View.GONE
    }

    private fun clearFields() {
        binding.lastNameEditText.text?.clear()
        binding.firstNameEditText.text?.clear()
        binding.middleNameEditText.text?.clear()
        binding.emailEditText.text?.clear()
        binding.passwordEditText.text?.clear()
        binding.subjectsEditText.setText("")
        binding.groupEditText.setText("")
        selectedSubjectId = -1
        selectedGroupIds.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}