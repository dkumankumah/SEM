package com.example.sem

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
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

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

    }

    private fun loadevent(): ArrayList<Event> {
        val events = ArrayList<Event>()
        events.add(Event(1, "Highschool Prom", "Who is gonna be prom King and queen?", Date(2024,10,2), ""))
        events.add(Event(2, "Football Game", "We are PENN STATE", Date(2024,10,1), ""))
        events.add(Event(3, "Keynote", "career fair", Date(2024,10,14), ""))
        events.add(Event(4, "Elections", "Presidents Elections", Date(2024,10,10), ""))

        return events
    }

}