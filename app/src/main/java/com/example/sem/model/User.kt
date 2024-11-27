package com.example.sem.model

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val role: String = "STUDENT",
    val yearGraduation: String = "",
    val attending: List<String> = emptyList(),
    val following: List<String> = emptyList()
)
