package com.likhachev.e_journal.data.model

data class CreateHomeworkRequest(
    val groupId: String,
    val subjectId: Int,
    val date: String,
    val description: String
)