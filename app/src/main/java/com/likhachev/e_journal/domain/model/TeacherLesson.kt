package com.likhachev.e_journal.domain.model

data class TeacherLesson(
    val number: Int,
    val time: String,
    val className: String,
    val classId: String,
    val subject: String,
    val subjectId: String,
    val classroom: String
)