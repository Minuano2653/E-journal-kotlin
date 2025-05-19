package com.likhachev.e_journal.domain.model

data class StudentLesson(
    val number: Int,
    val time: String,
    val teacher: String,
    val subject: String,
    val classroom: String,
    val homework: String
)
