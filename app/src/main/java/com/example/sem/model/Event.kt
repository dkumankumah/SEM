package com.example.sem.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import java.io.Serializable
import java.util.*

data class Event(
    var attendingCount: Int = 0,
    var dateCreated: Timestamp,
    var eventCategory: String = "",
    var eventDate: Timestamp,
    var eventDescription: String = "",
    var eventId: String = "",
    var eventManager: String = "",
    var eventName: String = "",
    var location: GeoPoint? = null,
    var numberAttending: Int = 0,
    var numberFollowing: Int = 0,
) : Serializable {
    // No-argument constructor for Firebase
    constructor() : this(0,   Timestamp.now(), "", Timestamp.now() , "", "", "", "", null, 0, 0)
}
