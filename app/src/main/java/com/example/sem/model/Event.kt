package com.example.sem.model

import java.io.Serializable


data class Event(
    var attendingCount: Int = 0,
    var eventDate: String = "",
    var eventCategory: String = "",
    var eventDescription: String = "",
    var eventId: String = "",
    var eventManager: String = "",
    var eventName: String = "",
    var eventTime: String = "",
    var location: String = "",
    var numberAttending: Int = 0,
    var numberFollowing: Int = 0,
    var docId: String = ""
) : Serializable {
    // No-argument constructor for Firebase
    constructor() : this(0,   "", "", "" , "", "", "", "", "", 0, 0, "")
}
