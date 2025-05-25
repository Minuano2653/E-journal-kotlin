package com.likhachev.e_journal.presentation.ui.teacher_groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.likhachev.e_journal.R
import com.likhachev.e_journal.data.model.TeacherGroupDto
import com.likhachev.e_journal.databinding.ItemClassBinding

class TeacherGroupsAdapter(
    private val onGroupClick: (TeacherGroupDto) -> Unit
) : ListAdapter<TeacherGroupDto, TeacherGroupsAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemClassBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group)
    }

    inner class GroupViewHolder(private val binding: ItemClassBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(group: TeacherGroupDto) {
            val context = binding.root.context

            binding.classNameTextView.text = group.groupName
            binding.subjectNameTextView.text = group.subjectName
            binding.studentCountTextView.text = group.studentCount.toString()

            binding.root.setOnClickListener {
                onGroupClick(group)
            }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<TeacherGroupDto>() {
        override fun areItemsTheSame(oldItem: TeacherGroupDto, newItem: TeacherGroupDto): Boolean {
            return oldItem.groupId == newItem.groupId && oldItem.subjectId == newItem.subjectId
        }

        override fun areContentsTheSame(oldItem: TeacherGroupDto, newItem: TeacherGroupDto): Boolean {
            return oldItem == newItem
        }
    }
}