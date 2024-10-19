package com.example.sem.model

import java.io.Serializable
import java.util.*

data class Event(
    var id: Int = 0,
    var eventName: String = "",
    var eventManager: String = "",
    var eventDescription: String = "",
    var eventDate: Date = Date(),
    var attendingCount: Int = 0,
    var dateCreated: Date = Date(),
    var forClass: List<Int> = emptyList(),
    var location: String = ""
) : Serializable {
    // No-argument constructor for Firebase
    constructor() : this(0, "", "", "", Date(), 0, Date(), emptyList(), "")
}
