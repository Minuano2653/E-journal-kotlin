package com.likhachev.e_journal.data.model

data class TeacherGroupDto(
    val groupId: String,
    val subjectId: Int,
    val groupName: String,
    val subjectName: String,
    val studentCount: Int
)