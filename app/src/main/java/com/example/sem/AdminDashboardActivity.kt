package com.example.sem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sem.model.Event
import com.example.sem.model.UpcomingEventAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date


class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: UpcomingEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

        db = FirebaseFirestore.getInstance()

        // View initialization
        val academicBtn = findViewById<Button>(R.id.btn_eduction)
        val clubsBtn = findViewById<Button>(R.id.clubs_btn)
        val extraBtn = findViewById<Button>(R.id.extra_btn)
        val charityBtn = findViewById<Button>(R.id.charity_btn)
        val addBtn = findViewById<Button>(R.id.add_event_btn)
        val rvEvents = findViewById<RecyclerView>(R.id.rv_upcomingEvents)
        val arrowIcon = findViewById<ImageView>(R.id.arrow_icon)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        //Intent extra
        val role = intent.getStringExtra("USER_ROLE")

        if (role != null) {
            Log.d("ROLE", role)
        }

        if(role == "STUDENT") {
            addBtn.visibility= View.INVISIBLE;
        }
        // Initialize RecyclerView and Adapter with empty list
        adapter = UpcomingEventAdapter(emptyList()) { event ->
            Toast.makeText(this, "Event ${event.eventName} is selected", Toast.LENGTH_LONG).show()
        }
        rvEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEvents.adapter = adapter

        fetchEvents()

        academicBtn.setOnClickListener {
            //get academic events list and send through intent
            val intent = Intent(this, ShowAllEvents::class.java)
            startActivity(intent)
        }

        clubsBtn.setOnClickListener {
            //get clubs events list and send through intent
            val intent = Intent(this, ShowAllEvents::class.java)
            startActivity(intent)
        }

        extraBtn.setOnClickListener {
            //get extracurricular events list and send through intent
            val intent = Intent(this, ShowAllEvents::class.java)
            startActivity(intent)
        }

        charityBtn.setOnClickListener {
            //get charity events list and send through intent
            val intent = Intent(this, ShowAllEvents::class.java)
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            val intent = Intent(this, EventFormActivity::class.java)
            startActivity(intent)
        }

        arrowIcon.setOnClickListener {
            val intent = Intent(this, ShowAllEvents::class.java)
            startActivity(intent)
        }

        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_list -> {
                    // Navigate to MyListActivity
                    startActivity(Intent(this, ShowMyEvents::class.java))
                    true
                }
                R.id.nav_following -> {
                    // Navigate to FollowingActivity
                    startActivity(Intent(this, ShowInterestedEvents::class.java))
                    true
                }
                R.id.nav_maps -> {
                    // Navigate to Maps
                    true
                }
                R.id.nav_account -> {
                    // Navigate to AccountActivity
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    private fun fetchEvents() {
        var eventId: Int
        db.collection("eventsForTest").get()
            .addOnSuccessListener { documents ->
                val eventsList = documents.mapNotNull { document ->
                    document.toObject(Event::class.java).apply { eventId = document.id.hashCode() }
                }
                adapter.updateEvents(eventsList)
            }
            .addOnFailureListener { exception ->
                Log.w("FirebaseEvents", "Error getting documents: ", exception)
            }
    }

//    private fun setupRecyclerView(events: List<Event>) {
//        val rvEvents = findViewById<RecyclerView>(R.id.rv_upcomingEvents)
//        val adapter = UpcomingEventAdapter(events) { event ->
//            Toast.makeText(this, "Event ${event.eventName} is selected", Toast.LENGTH_LONG).show()
//        }
//        rvEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        rvEvents.adapter = adapter
//    }

}