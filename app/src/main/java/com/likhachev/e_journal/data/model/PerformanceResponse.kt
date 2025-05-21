package com.likhachev.e_journal.data.model

data class PerformanceResponse(
    val quarterGrades: List<QuarterGrade>,
    val attendanceStats: AttendanceStats
)
