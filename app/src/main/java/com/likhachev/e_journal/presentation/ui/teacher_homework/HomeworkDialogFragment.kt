package com.likhachev.e_journal.presentation.ui.teacher_homework

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.DialogFragmentHomeworkBinding
import com.likhachev.e_journal.presentation.viewmodel.HomeworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class HomeworkDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentHomeworkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeworkViewModel by viewModels()

    private val dateFormatDisplay = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    companion object {
        const val TAG = "HomeworkDialog"
        private const val ARG_GROUP_ID = "group_id"
        private const val ARG_GROUP_NAME = "group_name"
        private const val ARG_SUBJECT_ID = "subject_id"
        private const val ARG_DATE = "date"

        fun newInstance(groupId: String, groupName: String, subjectId: Int, date: String): HomeworkDialogFragment {
            val fragment = HomeworkDialogFragment()
            val args = Bundle().apply {
                putString(ARG_GROUP_ID, groupId)
                putString(ARG_GROUP_NAME, groupName)
                putInt(ARG_SUBJECT_ID, subjectId)
                putString(ARG_DATE, date)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFragmentHomeworkBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)

        binding.saveButton.setOnClickListener {
            val description = binding.homeworkEditText.text.toString()
            viewModel.saveHomework(description)
        }
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        setupUI()
        observeViewModel()
        initializeViewModel()

        return dialog.create()
    }

    private fun setupUI() {
        val groupName = arguments?.getString(ARG_GROUP_NAME) ?: ""
        val date = arguments?.getString(ARG_DATE) ?: ""

        binding.groupTextView.text = getString(R.string.homework_group_text, groupName)

        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            val formattedDate = if (parsedDate != null) {
                dateFormatDisplay.format(parsedDate)
            } else {
                date
            }
            binding.homeworkDateTextView.text = getString(R.string.homework_date_text, formattedDate)
        } catch (e: Exception) {
            binding.homeworkDateTextView.text = getString(R.string.homework_date_text, date)
        }
    }

    private fun initializeViewModel() {
        val groupId = arguments?.getString(ARG_GROUP_ID) ?: ""
        val subjectId = arguments?.getInt(ARG_SUBJECT_ID) ?: 0
        val date = arguments?.getString(ARG_DATE) ?: ""

        viewModel.initialize(groupId, subjectId, date)
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            handleUiState(state)
        }

        viewModel.messageEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                if (message.contains("задано") || message.contains("обновлено")) {
                    dismiss()
                }
            }
        }
    }


    private fun handleUiState(state: HomeworkDialogUiState) {
        when (state) {
            is HomeworkDialogUiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.homeworkEditText.isEnabled = false
                binding.saveButton.isEnabled = false
                binding.cancelButton.isEnabled = false
            }

            is HomeworkDialogUiState.HomeworkLoaded -> {
                binding.progressBar.visibility = View.GONE
                binding.homeworkEditText.isEnabled = true
                binding.homeworkEditText.setText(state.homework.description)
                binding.saveButton.isEnabled = true
                binding.cancelButton.isEnabled = true
            }

            is HomeworkDialogUiState.NoHomework -> {
                binding.progressBar.visibility = View.GONE
                binding.homeworkEditText.isEnabled = true
                //binding.homeworkEditText.setText("")
                binding.saveButton.isEnabled = true
                binding.cancelButton.isEnabled = true
            }

            is HomeworkDialogUiState.Saving -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.homeworkEditText.isEnabled = false
                binding.saveButton.isEnabled = false
                binding.cancelButton.isEnabled = false
            }

            is HomeworkDialogUiState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.homeworkEditText.isEnabled = true
                binding.saveButton.isEnabled = true
                binding.cancelButton.isEnabled = true
            }

            is HomeworkDialogUiState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.homeworkEditText.isEnabled = true
                binding.saveButton.isEnabled = true
                binding.cancelButton.isEnabled = true
            }

            is HomeworkDialogUiState.Idle -> {
                Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}