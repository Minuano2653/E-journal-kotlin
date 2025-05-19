package com.likhachev.e_journal.presentation.ui.student_schedule

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.likhachev.e_journal.databinding.FragmentStudentScheduleBinding
import com.likhachev.e_journal.presentation.DateChangeListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StudentScheduleFragment: Fragment() {
    private var _binding: FragmentStudentScheduleBinding? = null
    private val binding get() = _binding!!

    private var dateChangeListener: DateChangeListener? = null
    private var currentDate: Date = Calendar.getInstance().time
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

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
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        val formattedDate = dateFormat.format(currentDate)

        dateChangeListener?.onDateChanged(formattedDate)

        // Обновляем данные расписания для новой даты
        loadScheduleForDate(formattedDate)
    }

    private fun loadScheduleForDate(date: String) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}