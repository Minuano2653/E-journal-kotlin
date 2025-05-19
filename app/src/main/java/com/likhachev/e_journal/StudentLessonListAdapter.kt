package com.likhachev.e_journal

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.likhachev.e_journal.databinding.ItemStudentLessonBinding
import com.likhachev.e_journal.domain.model.StudentLesson

class StudentLessonListAdapter : ListAdapter<StudentLesson, StudentLessonListAdapter.LessonViewHolder>(LessonDiffCallback()) {

    private val expandedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemStudentLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = getItem(position)
        val isExpanded = expandedItems.contains(position)

        holder.bind(lesson, isExpanded)

        holder.binding.cardView.setOnClickListener {
            if (isExpanded) {
                expandedItems.remove(position)
            } else {
                expandedItems.add(position)
            }
            notifyItemChanged(position)
        }
    }

    class LessonViewHolder(val binding: ItemStudentLessonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: StudentLesson, isExpanded: Boolean) {

            val context = binding.root.context
            binding.lessonNumberTextView.text = context.getString(R.string.lesson_number_text_view, lesson.number.toString())
            binding.lessonTimeTextView.text = lesson.time
            binding.teacherNameTextView.text = lesson.teacher
            binding.subjectNameTextView.text = lesson.subject
            binding.classroomValueTextView.text = lesson.classroom
            binding.homeworkValueTextView.text = lesson.homework

            binding.homeworkValueTextView.maxLines = if (isExpanded) Integer.MAX_VALUE else 1
            binding.homeworkValueTextView.ellipsize = if (isExpanded) null else TextUtils.TruncateAt.END
        }

    }

    class LessonDiffCallback : DiffUtil.ItemCallback<StudentLesson>() {
        override fun areItemsTheSame(oldItem: StudentLesson, newItem: StudentLesson): Boolean {
            return oldItem.number == newItem.number // Сравниваем по уникальному ID
        }

        override fun areContentsTheSame(oldItem: StudentLesson, newItem: StudentLesson): Boolean {
            return oldItem == newItem // Полное сравнение объектов
        }
    }
}