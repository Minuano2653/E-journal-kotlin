package com.likhachev.e_journal.data.model

import com.likhachev.e_journal.domain.model.TeacherLesson

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

    fun toDomain(): TeacherLesson =
        TeacherLesson(
            number = lessonNumber,
            time = timeRange,
            groupName = group.name,
            groupId = group.id,
            subjectName = subject.name,
            subjectId = subject.id,
            classroom = classroom
        )
}