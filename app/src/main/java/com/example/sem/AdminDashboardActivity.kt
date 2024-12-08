package com.example.sem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sem.model.Event
import com.example.sem.model.UpcomingEventAdapter
import com.example.sem.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: UpcomingEventAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var addBtn: Button
    public var role:String = ""
        get() {
            return field;
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

        db = FirebaseFirestore.getInstance()

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        userId = firebaseAuth.currentUser!!.uid

        // View initialization
        addBtn = findViewById<Button>(R.id.add_event_btn)
        addBtn.visibility = View.INVISIBLE;

        checkAdmin()

        val academicBtn = findViewById<Button>(R.id.btn_eduction)
        val clubsBtn = findViewById<Button>(R.id.clubs_btn)
        val extraBtn = findViewById<Button>(R.id.extra_btn)
        val charityBtn = findViewById<Button>(R.id.charity_btn)
        val rvEvents = findViewById<RecyclerView>(R.id.rv_upcomingEvents)
        val arrowIcon = findViewById<ImageView>(R.id.arrow_icon)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Initialize RecyclerView and Adapter with empty list
        adapter = UpcomingEventAdapter(emptyList()) { event ->
            val intent = Intent(
                this,
                MapHostActivity::class.java
            )
            intent.putExtra("selected_event", event)
            startActivity(intent)
        }
        rvEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEvents.adapter = adapter


        fetchEvents()

        academicBtn.setOnClickListener {
            //get academic events list and send through intent
            val intent = Intent(this, ShowAllEvents::class.java)
            intent.putExtra("type", "Academics")
            startActivity(intent)
        }

        clubsBtn.setOnClickListener {
            //get clubs events list and send through intent
            val intent = Intent(this, ShowAllEvents::class.java)
            intent.putExtra("type", "Clubs")
            startActivity(intent)
        }

        extraBtn.setOnClickListener {
            //get extracurricular events list and send through intent
            val intent = Intent(this, ShowAllEvents::class.java)
            intent.putExtra("type", "Athletics")
            startActivity(intent)
        }

        charityBtn.setOnClickListener {
            //get charity events list and send through intent
            val intent = Intent(this, ShowAllEvents::class.java)
            intent.putExtra("type", "Service")
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            val intent = Intent(this, EventFormActivity::class.java)
            startActivity(intent)
        }

        arrowIcon.setOnClickListener {
            val intent = Intent(this, ShowAllEvents::class.java)
            intent.putExtra("type", "All")
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
                    val intent = Intent(this, MapHostActivity::class.java)
                    intent.putExtra("ALL_EVENTS", true) // If you need to pass user data
                    startActivity(intent)
//                    startActivity(Intent(this, MapHostActivity::class.java))
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

    private fun checkAdmin() {
//        addBtn = findViewById<Button>(R.id.add_event_btn)

        // Fething user data from the firestore database
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)!!
                    role = user.role

                    if(role == "ADMIN") {
                        addBtn.visibility= View.VISIBLE;
                    }
                }
            }
    }


}