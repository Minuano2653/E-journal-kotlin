package com.likhachev.e_journal.presentation.ui.teacher_journal

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.likhachev.e_journal.R
import com.likhachev.e_journal.data.model.StudentJournalEntry
import com.likhachev.e_journal.databinding.FragmentTeacherJournalBinding
import com.likhachev.e_journal.presentation.viewmodel.TeacherJournalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TeacherJournalFragment: Fragment() {

    private var _binding: FragmentTeacherJournalBinding? = null
    private val binding get() = _binding!!

    private val args: TeacherJournalFragmentArgs by navArgs()
    private val viewModel: TeacherJournalViewModel by viewModels()
    private lateinit var adapter: JournalEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Обработка системной кнопки "назад"
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeViewModel()

        // Инициализируем ViewModel с переданными параметрами
        val lesson = args.teacherLesson
        Log.d("DATE", lesson.date)
        viewModel.initialize(lesson.groupId, lesson.subjectId, lesson.date)
    }

    private fun setupRecyclerView() {
        adapter = JournalEntryAdapter { student ->
            onStudentClick(student)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TeacherJournalFragment.adapter
        }
    }

    private fun setupFab() {
        binding.changeDateFab.setOnClickListener {
            showDatePickerDialog()
        }
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
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                viewModel.setDate(selectedCalendar.time)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.journalState.collect { state ->
                    when (state) {
                        is TeacherJournalUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.GONE
                        }
                        is TeacherJournalUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.notFoundLayout.visibility = View.GONE

                            val sortedList = state.journalData.students.sortedBy { it.studentName }
                            adapter.submitList(sortedList)
                        }
                        is TeacherJournalUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.notFoundLayout.visibility = View.VISIBLE
                        }
                        is TeacherJournalUiState.Idle -> Unit
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

    private fun onStudentClick(student: StudentJournalEntry) {
        // Здесь можно добавить логику для редактирования оценки/посещаемости
        // Например, открыть диалог для ввода оценки
        Toast.makeText(requireContext(), "Клик по студенту: ${student.studentName}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}