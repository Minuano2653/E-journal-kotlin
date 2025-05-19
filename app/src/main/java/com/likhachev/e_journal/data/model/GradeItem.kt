package com.likhachev.e_journal.data.model

data class GradeItem(
    val id: Long,
    val date: String,
    val grade: Int?,
    val attendanceStatus: String
)