package com.likhachev.e_journal.data.model

data class CreateTeacherRequest(
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val groupIdList: List<String>,
    val subjectId: Int
)