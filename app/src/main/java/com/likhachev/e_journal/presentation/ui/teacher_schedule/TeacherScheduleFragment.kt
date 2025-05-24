package com.likhachev.e_journal.presentation.ui.teacher_schedule

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.likhachev.e_journal.R
import com.likhachev.e_journal.domain.model.TeacherGroup
import com.likhachev.e_journal.databinding.FragmentTeacherScheduleBinding
import com.likhachev.e_journal.domain.model.TeacherLesson
import com.likhachev.e_journal.presentation.ui.teacher_homework.HomeworkDialogFragment
import com.likhachev.e_journal.presentation.viewmodel.TeacherScheduleViewModel
import com.likhachev.e_journal.utils.DateChangeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TeacherScheduleFragment : Fragment() {
    private var _binding: FragmentTeacherScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeacherScheduleViewModel by viewModels()
    private lateinit var adapter: TeacherLessonListAdapter

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
        _binding = FragmentTeacherScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupButtons()
    }

    private fun setupButtons() {
        binding.changeDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        binding.updateButton.setOnClickListener {
            viewModel.getScheduleForDay()
        }
    }

    private fun setupRecyclerView() {
        adapter = TeacherLessonListAdapter(
            onSetHomeworkClick = { lesson ->
                showHomeworkDialog(lesson)
            },
            onGoToJournalClick = { lesson ->
                navigateToJournal(
                    TeacherGroup(lesson.groupId, lesson.subjectId, lesson.groupName, lesson.date)
                )
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TeacherScheduleFragment.adapter
        }
    }

    private fun navigateToJournal(lesson: TeacherGroup) {
        val action = TeacherScheduleFragmentDirections
            .actionTeacherScheduleFragmentToTeacherJournalFragment(lesson)

        findNavController().navigate(action)
    }

    private fun showHomeworkDialog(lesson: TeacherLesson) {
        HomeworkDialogFragment.newInstance(lesson)
            .show(childFragmentManager, HomeworkDialogFragment.TAG)
    }

    private fun showDatePickerDialog() {
        val currentDate = viewModel.currentDate.value
        val calendar = Calendar.getInstance().apply { time = currentDate }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.CustomDatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Создаем дату из выбранных значений
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                // Передаем выбранную дату в ViewModel
                viewModel.setDate(selectedCalendar.time)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scheduleState.collect { state ->
                    when (state) {
                        is TeacherScheduleUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.GONE
                        }
                        is TeacherScheduleUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.notFoundLayout.visibility = View.GONE

                            adapter.submitList(state.lessons)
                        }
                        is TeacherScheduleUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.VISIBLE
                        }
                        is TeacherScheduleUiState.Idle -> {Unit}
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}