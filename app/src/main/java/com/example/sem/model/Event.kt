package com.example.sem.model

import java.io.Serializable
import java.util.*

data class Event(
    val id: Int,
    val eventName: String,
    val eventManager: String,
    val eventDescription: String,
    val eventDate: Date,
    val attendingCount: Int,
    val dateCreated: Date,
    val forClass: Array<Int>,
    val location: String,
//    val imageUrl: String
) : Serializable
