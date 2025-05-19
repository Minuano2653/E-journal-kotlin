package com.likhachev.e_journal.presentation.ui.student_grades

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.likhachev.e_journal.data.model.GradeItem
import com.likhachev.e_journal.databinding.ItemGradeBinding
import java.text.SimpleDateFormat
import java.util.Locale

class StudentGradesAdapter : ListAdapter<GradeItem, StudentGradesAdapter.GradeViewHolder>(GradeDiffCallback()) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val binding = ItemGradeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GradeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GradeViewHolder(private val binding: ItemGradeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gradeItem: GradeItem) {
            val date = try {
                val parsedDate = dateFormat.parse(gradeItem.date)
                parsedDate?.let { displayFormat.format(it) } ?: gradeItem.date
            } catch (e: Exception) {
                gradeItem.date
            }

            binding.dateOfGradeTextView.text = date

            binding.subjectGradeTextView.text = gradeItem.grade?.toString() ?: gradeItem.attendanceStatus
        }
    }

    class GradeDiffCallback : DiffUtil.ItemCallback<GradeItem>() {
        override fun areItemsTheSame(oldItem: GradeItem, newItem: GradeItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GradeItem, newItem: GradeItem): Boolean {
            return oldItem == newItem
        }
    }
}