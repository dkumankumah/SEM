package com.example.sem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sem.model.Event
import com.example.sem.model.UpcomingEventAdapter
import java.util.Date


class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

        var events = loadevent()
        Log.d("EventListSize", "Size: ${events.size}")


        // View initialization
        val academicBtn = findViewById<Button>(R.id.btn_eduction)
        val clubsBtn = findViewById<Button>(R.id.clubs_btn)
        val extraBtn = findViewById<Button>(R.id.extra_btn)
        val charityBtn = findViewById<Button>(R.id.charity_btn)
        val addBtn = findViewById<Button>(R.id.add_event_btn)
        var rvEvents = findViewById<RecyclerView>(R.id.rv_upcomingEvents)

        var adapter = UpcomingEventAdapter(events) { event ->
            Toast.makeText(this, "Event ${event.eventName} is selected", Toast.LENGTH_LONG).show()
        }

        rvEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false )
        rvEvents.adapter = adapter

        addBtn.setOnClickListener {
            val intent = Intent(this, EventFormActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadevent(): ArrayList<Event> {
        val events = ArrayList<Event>()
        events.add(Event(
            id = 1,
            eventName = "Highschool Prom",
            eventManager = "Prom Committee",
            eventDescription = "Who is gonna be prom King and queen?",
            eventDate = Date(2024 - 1900, 9, 2),
            attendingCount = 0,
            dateCreated = Date(),
            forClass = listOf(11, 12), // juniors and seniors
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
            forClass = listOf(9, 10, 11, 12), // All high school classes
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
            forClass = listOf(11, 12), // Juniors and seniors
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
            forClass = listOf(9, 10, 11, 12), // All high school classes
            location = "School Hallways"
        ))

        return events
    }

}