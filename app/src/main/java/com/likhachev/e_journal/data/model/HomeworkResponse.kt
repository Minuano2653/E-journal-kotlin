package com.likhachev.e_journal.data.model

data class HomeworkResponse(
    val id: Int,
    val assignmentId: Int,
    val subjectId: Int,
    val date: String,
    val description: String
)