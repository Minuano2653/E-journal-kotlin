package com.likhachev.e_journal.data.model

data class CreateStudentRequest(
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val groupId: String
)