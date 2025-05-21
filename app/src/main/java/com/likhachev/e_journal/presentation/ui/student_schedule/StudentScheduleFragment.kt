package com.likhachev.e_journal.presentation.ui.student_schedule

import android.app.DatePickerDialog
import android.content.Context
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
import com.likhachev.e_journal.domain.model.StudentLesson
import com.likhachev.e_journal.data.model.Lesson
import com.likhachev.e_journal.databinding.FragmentStudentScheduleBinding
import com.likhachev.e_journal.presentation.viewmodel.StudentScheduleViewModel
import com.likhachev.e_journal.utils.DateChangeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class StudentScheduleFragment: Fragment() {
    private var _binding: FragmentStudentScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudentScheduleViewModel by viewModels()
    private lateinit var adapter: StudentLessonListAdapter

    private var dateChangeListener: DateChangeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        var parentFragment = parentFragment
        while (parentFragment != null) {
            if (parentFragment is DateChangeListener) {
                dateChangeListener = parentFragment
                break
            }
            parentFragment = parentFragment.parentFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupButtons()
    }

    private fun setupButtons() {
        // Настройка кнопки выбора даты
        binding.changeDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Добавление обработчика для кнопки обновления
        binding.updateButton.setOnClickListener {
            viewModel.getScheduleForDay()
        }

    }

    private fun setupRecyclerView() {
        adapter = StudentLessonListAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@StudentScheduleFragment.adapter
        }
    }
    private fun showDatePickerDialog() {
        val currentDate = viewModel.currentDate.value
        val calendar = Calendar.getInstance().apply { time = currentDate }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), R.style.CustomDatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Создаем дату из выбранных значений
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                // Передаем выбранную дату в ViewModel
                viewModel.setDate(selectedCalendar.time)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scheduleState.collect { state ->
                    when (state) {
                        is ScheduleUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.GONE
                        }
                        is ScheduleUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.notFoundLayout.visibility = View.GONE

                            val studentLessons = mapLessonsToStudentLessons(state.lessons)
                            adapter.submitList(studentLessons)
                        }
                        is ScheduleUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.VISIBLE
                        }
                        is ScheduleUiState.Idle -> Unit
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentDate.collect {
                    val displayDate = viewModel.getDisplayFormattedDate()
                    dateChangeListener?.onDateChanged(displayDate)
                }
            }
        }
    }

    private fun mapLessonsToStudentLessons(lessons: List<Lesson>): List<StudentLesson> {
        return lessons.map { lesson ->
            StudentLesson(
                number = lesson.lessonNumber,
                time = lesson.timeRange,
                teacher = lesson.teacher,
                subject = lesson.subject,
                classroom = lesson.classroom,
                homework = lesson.homeworkText
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
