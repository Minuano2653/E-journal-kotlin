package com.likhachev.e_journal.data.model

data class AttendanceStats(
    val present: Int,
    val absent: Int,
    val excused: Int
)