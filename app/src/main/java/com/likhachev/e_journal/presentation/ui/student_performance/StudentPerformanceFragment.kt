package com.likhachev.e_journal.presentation.ui.student_performance

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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentStudentPerformanceBinding
import com.likhachev.e_journal.presentation.viewmodel.StudentPerformanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentPerformanceFragment: Fragment() {
    private var _binding: FragmentStudentPerformanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudentPerformanceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentPerformanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.subjectSpinner.setOnClickListener {
            showSubjectSelectionDialog()
        }

        binding.updateButton.setOnClickListener {
            viewModel.loadPerformance()
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
                viewModel.performanceState.collect { state ->
                    when (state) {
                        is StudentPerformanceUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.chartsContainer.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.GONE
                        }
                        is StudentPerformanceUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.chartsContainer.visibility = View.VISIBLE
                            binding.notFoundLayout.visibility = View.GONE

                            setupGradesChart(state.performance.quarterGrades.map { it.averageGrade }.toList())
                            setupAttendanceChart(
                                state.performance.attendanceStats.present,
                                state.performance.attendanceStats.absent,
                                state.performance.attendanceStats.excused
                            )
                        }
                        is StudentPerformanceUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.chartsContainer.visibility = View.GONE
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

    private fun setupGradesChart(quarterGrades: List<Double>) {
        val entries = quarterGrades.mapIndexed { index, grade ->
            BarEntry(index.toFloat(), grade.toFloat())
        }

        val dataSet = BarDataSet(entries, "Средний балл").apply {
            color = resources.getColor(R.color.orange, null)
            valueTextColor = resources.getColor(R.color.brown, null)
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)

        binding.averageGradesChart.apply {
            data = barData
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)

            val xAxis = xAxis
            val quarters = listOf("1 четверть", "2 четверть", "3 четверть", "4 четверть")
            xAxis.valueFormatter = IndexAxisValueFormatter(quarters)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.textColor = resources.getColor(R.color.brown, null)

            axisLeft.axisMinimum = 2f
            axisLeft.axisMaximum = 5f
            axisLeft.textColor = resources.getColor(R.color.brown, null)

            axisRight.isEnabled = false

            animateY(1000)
            invalidate()
        }
    }

    private fun setupAttendanceChart(present: Int, absent: Int, excused: Int) {
        val entries = listOf(
            BarEntry(0f, present.toFloat()),
            BarEntry(1f, absent.toFloat()),
            BarEntry(2f, excused.toFloat())
        )

        val dataSet = BarDataSet(entries, "Посещаемость").apply {
            colors = listOf(
                resources.getColor(R.color.orange, null),
                resources.getColor(R.color.orange, null),
                resources.getColor(R.color.orange, null)
            )
            valueTextColor = resources.getColor(R.color.brown, null)
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)

        binding.presenceAndAbsenceChart.apply {
            data = barData
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)

            val xAxis = xAxis
            val labels = listOf("Присутствовал", "Отсутствовал", "По уважит. причине")
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.textColor = resources.getColor(R.color.brown, null)

            axisLeft.axisMinimum = 0f
            axisLeft.textColor = resources.getColor(R.color.brown, null)

            axisRight.isEnabled = false

            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}