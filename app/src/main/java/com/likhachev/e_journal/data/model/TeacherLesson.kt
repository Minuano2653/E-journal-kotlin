package com.likhachev.e_journal.data.model

data class TeacherLesson(
    val lessonNumber: Int,
    val startTime: String,
    val endTime: String,
    val group: Group,
    val subject: Subject,
    val classroom: String
) {
    val timeRange: String
        get() = "$startTime - $endTime"
}