package com.likhachev.e_journal.presentation.ui.teacher_schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.likhachev.e_journal.R
import com.likhachev.e_journal.data.model.TeacherLesson
import com.likhachev.e_journal.databinding.ItemTeacherLessonBinding

class TeacherLessonListAdapter(
    private val onSetHomeworkClick: (TeacherLesson) -> Unit
) : ListAdapter<TeacherLesson, TeacherLessonListAdapter.LessonViewHolder>(
    LessonDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemTeacherLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = getItem(position)
        holder.bind(lesson)
    }

    inner class LessonViewHolder(val binding: ItemTeacherLessonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: TeacherLesson) {
            val context = binding.root.context

            binding.lessonNumberTextView.text = context.getString(R.string.lesson_number_text_view, lesson.lessonNumber.toString())
            binding.lessonTimeTextView.text = lesson.timeRange
            binding.classNameTextView.text = lesson.group.name
            binding.subjectNameTextView.text = lesson.subject.name
            binding.classroomValueTextView.text = lesson.classroom

            binding.root.setOnClickListener { view ->
                showPopupMenu(view, lesson)
            }
        }

        private fun showPopupMenu(view: View, lesson: TeacherLesson) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.teacher_lesson_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_go_to_journal -> {
                        // Действие для перехода к журналу
                        // В будущем можно будет использовать lesson.group.id и lesson.subject.id
                        true
                    }
                    R.id.action_set_homework -> {
                        onSetHomeworkClick(lesson)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    class LessonDiffCallback : DiffUtil.ItemCallback<TeacherLesson>() {
        override fun areItemsTheSame(oldItem: TeacherLesson, newItem: TeacherLesson): Boolean {
            return oldItem.lessonNumber == newItem.lessonNumber &&
                    oldItem.group.id == newItem.group.id &&
                    oldItem.subject.id == newItem.subject.id
        }

        override fun areContentsTheSame(oldItem: TeacherLesson, newItem: TeacherLesson): Boolean {
            return oldItem == newItem
        }
    }
}