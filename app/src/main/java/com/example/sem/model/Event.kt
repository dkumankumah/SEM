package com.example.sem.model

import java.util.Date

data class Event(
    val id: Int,
    val eventName: String,
    val description: String,
    val date: Date,
    val imageUrl: String
)
//data class Event(
//    var eventId: Int,
//    var eventName: String,
//    var description: String?,
//    var date: Date?,
//    var imageUrl: String?
//)