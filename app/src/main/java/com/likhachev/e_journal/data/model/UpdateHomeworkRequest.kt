package com.likhachev.e_journal.data.model

data class UpdateHomeworkRequest(
    val homeworkId: Int,
    val description: String
)