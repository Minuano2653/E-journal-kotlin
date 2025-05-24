package com.likhachev.e_journal.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TeacherLesson(
    val number: Int,
    val time: String,
    val groupName: String,
    val groupId: String,
    val subjectName: String,
    val subjectId: Int,
    val classroom: String,
    val date: String
): Parcelable