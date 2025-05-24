package com.likhachev.e_journal.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TeacherGroup(
    val groupId: String,
    val subjectId: Int,
    val groupName: String,
    val date: String
): Parcelable