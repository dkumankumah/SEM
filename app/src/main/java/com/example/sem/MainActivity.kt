package com.example.sem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sem.model.UpcomingEventAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sem.model.Event
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: UpcomingEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val events = loadEvents()
        Log.d("EventListSize", "Size: ${events.size}")

        db = FirebaseFirestore.getInstance()

        // Initialize RecyclerView and Adapter with empty list
        val rvEvents = findViewById<RecyclerView>(R.id.rv_upcomingEvents)
        adapter = UpcomingEventAdapter(emptyList()) { event ->
            Toast.makeText(this, "Event ${event.eventName} is selected", Toast.LENGTH_LONG).show()
        }
        rvEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEvents.adapter = adapter

        fetchEvents()

        // View initialization
        val academicBtn = findViewById<Button>(R.id.btn_eduction)
        val clubsBtn = findViewById<Button>(R.id.clubs_btn)
        val extraBtn = findViewById<Button>(R.id.extra_btn)
        val charityBtn = findViewById<Button>(R.id.charity_btn)
        val addBtn = findViewById<Button>(R.id.add_event_btn)

        rvEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEvents.adapter = adapter

        addBtn.setOnClickListener {
            val intent = Intent(this, EventFormActivity::class.java)
            startActivity(intent)
        }

        // Set click listeners for other buttons as needed
    }

    private fun fetchEvents() {
        db.collection("events").get()
            .addOnSuccessListener { documents ->
                val eventsList = documents.mapNotNull { document ->
                    document.toObject(Event::class.java).apply { id = document.id.hashCode() }
                }
                adapter.updateEvents(eventsList)
            }
            .addOnFailureListener { exception ->
                Log.w("FirebaseEvents", "Error getting documents: ", exception)
            }
    }

    private fun setupRecyclerView(events: List<Event>) {
        val rvEvents = findViewById<RecyclerView>(R.id.rv_upcomingEvents)
        val adapter = UpcomingEventAdapter(events) { event ->
            Toast.makeText(this, "Event ${event.eventName} is selected", Toast.LENGTH_LONG).show()
        }
        rvEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEvents.adapter = adapter
    }

    private fun loadEvents(): ArrayList<Event> {
        val events = ArrayList<Event>()
        events.add(Event(
            id = 1,
            eventName = "Highschool Prom",
            eventManager = "Prom Committee",
            eventDescription = "Who is gonna be prom King and Queen?",
            eventDate = Date(2024 - 1900, 9, 2),
            attendingCount = 0,
            dateCreated = Date(),
            forClass = listOf(11, 12),
            location = "School Gymnasium"
        ))
        events.add(Event(
            id = 2,
            eventName = "Football Game",
            eventManager = "Athletics Department",
            eventDescription = "We are PENN STATE",
            eventDate = Date(2024 - 1900, 9, 1),
            attendingCount = 0,
            dateCreated = Date(),
            forClass = listOf(9, 10, 11, 12),
            location = "Football Stadium"
        ))
        events.add(Event(
            id = 3,
            eventName = "Keynote",
            eventManager = "Career Services",
            eventDescription = "Career fair",
            eventDate = Date(2024 - 1900, 9, 14),
            attendingCount = 0,
            dateCreated = Date(),
            forClass = listOf(11, 12),
            location = "Auditorium"
        ))
        events.add(Event(
            id = 4,
            eventName = "Elections",
            eventManager = "Student Council",
            eventDescription = "Presidents Elections",
            eventDate = Date(2024 - 1900, 9, 10),
            attendingCount = 0,
            dateCreated = Date(),
            forClass = listOf(9, 10, 11, 12),
            location = "School Hallways"
        ))
        return events
    }
}
