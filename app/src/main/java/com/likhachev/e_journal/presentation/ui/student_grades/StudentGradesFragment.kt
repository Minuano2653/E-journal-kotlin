package com.likhachev.e_journal.presentation.ui.student_grades

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentStudentGradesBinding
import com.likhachev.e_journal.presentation.viewmodel.StudentGradesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentGradesFragment : Fragment() {
    private var _binding: FragmentStudentGradesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudentGradesViewModel by viewModels()
    private lateinit var adapter: StudentGradesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentGradesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = StudentGradesAdapter()
        binding.gradesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@StudentGradesFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.subjectSpinner.setOnClickListener {
            showSubjectSelectionDialog()
        }

        binding.monthSpinner.setOnClickListener {
            showMonthSelectionDialog()
        }
        binding.updateButton.setOnClickListener {
            viewModel.loadGrades()
        }
    }

    private fun showSubjectSelectionDialog() {
        val subjects = viewModel.subjects.value
        val subjectNames = subjects.map { it.name }.toTypedArray()

        val currentSubjectIndex = subjects.indexOfFirst { it.id == viewModel.selectedSubject.value.id }

        AlertDialog.Builder(requireContext(), R.style.CustomDatePickerDialogTheme)
            .setTitle("Выберите предмет")
            .setSingleChoiceItems(subjectNames, currentSubjectIndex) { dialog, which ->
                viewModel.selectSubject(subjects[which])
                dialog.dismiss()
            }
            .show()
    }

    private fun showMonthSelectionDialog() {
        val months = viewModel.months.value
        val monthNames = months.map { it.name }.toTypedArray()

        val currentMonthIndex = months.indexOfFirst { it.number == viewModel.selectedMonth.value.number }

        AlertDialog.Builder(requireContext(), R.style.CustomDatePickerDialogTheme)
            .setTitle("Выберите месяц")
            .setSingleChoiceItems(monthNames, currentMonthIndex) { dialog, which ->
                viewModel.selectMonth(months[which])
                dialog.dismiss()
            }
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedSubject.collect { subject ->
                    binding.subjectSpinner.text = subject.name
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedMonth.collect { month ->
                    binding.monthSpinner.text = month.name
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gradesState.collect { state ->
                    when (state) {
                        is StudentGradesUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.gradesRecyclerView.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.GONE
                        }
                        is StudentGradesUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.gradesRecyclerView.visibility = View.VISIBLE
                            binding.notFoundLayout.visibility = View.GONE
                            adapter.submitList(state.grades)
                        }
                        is StudentGradesUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.gradesRecyclerView.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.VISIBLE
                        }
                        else -> Unit
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}