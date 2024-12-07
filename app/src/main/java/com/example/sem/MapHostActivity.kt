package com.example.sem

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sem.model.Event
import com.google.android.material.bottomnavigation.BottomNavigationView

class MapHostActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_map_host)

        bottomNav = findViewById(R.id.bottomNav)


        // Get the intent from the sending activity
        val myIntent = intent

        //Get navigation value intent
        val navIntent = myIntent.getBooleanExtra("ALL_EVENTS", false)


        if(navIntent) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentLayout, MapsFragment()).commit()
        } else {

            // Get the event object from the intent's extras
            val selectedEvent = myIntent.getSerializableExtra("selected_event") as Event
            intent.putExtra("event", selectedEvent);

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentLayout, EventOnClickFragment()).commit()
        }

        // Set the initial selected item
        bottomNav.selectedItemId = R.id.nav_maps

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
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
}