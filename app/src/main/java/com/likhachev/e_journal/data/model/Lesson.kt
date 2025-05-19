package com.likhachev.e_journal.data.model

data class Lesson(
    val lessonNumber: Int,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val teacher: String,
    val classroom: String,
    val homework: String? = null
) {
    val timeRange: String
        get() = "$startTime - $endTime"

    val homeworkText: String
        get() = homework ?: "не задано"
}