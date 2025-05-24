package com.likhachev.e_journal.presentation.ui.teacher_journal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.likhachev.e_journal.data.model.StudentJournalEntry
import com.likhachev.e_journal.databinding.ItemJournalEntryBinding

class JournalEntryAdapter(
    private val onStudentClick: (StudentJournalEntry) -> Unit
) : ListAdapter<StudentJournalEntry, JournalEntryAdapter.JournalEntryViewHolder>(
    JournalEntryDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalEntryViewHolder {
        val binding = ItemJournalEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return JournalEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JournalEntryViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)
    }

    inner class JournalEntryViewHolder(private val binding: ItemJournalEntryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentJournalEntry) {
            binding.studentTextView.text = student.studentName

            // Отображение оценки и посещаемости
            val gradeText = when {
                student.grade != null -> student.grade.toString()
                student.attendanceStatus != null -> student.attendanceStatus
                else -> "Нет\nотметки"
            }
            binding.subjectGradeTextView.text = gradeText

            binding.root.setOnClickListener {
                onStudentClick(student)
            }
        }
    }

    class JournalEntryDiffCallback : DiffUtil.ItemCallback<StudentJournalEntry>() {
        override fun areItemsTheSame(oldItem: StudentJournalEntry, newItem: StudentJournalEntry): Boolean {
            return oldItem.studentId == newItem.studentId
        }

        override fun areContentsTheSame(oldItem: StudentJournalEntry, newItem: StudentJournalEntry): Boolean {
            return oldItem == newItem
        }
    }
}