package com.likhachev.e_journal.data.model

data class CreateGroupRequest(
    val name: String,
    val startYear: Int,
    val endYear: Int
)