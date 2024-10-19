package com.example.sem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val showEventsBtn = findViewById<Button>(R.id.explicit_intent)
        val adminDashboardBtn = findViewById<Button>(R.id.admin_intent)


        showEventsBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, ShowAllEvents::class.java)
            startActivity(intent)
        }

        adminDashboardBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AdminDashboardActivity::class.java)
            startActivity(intent)
        }
    }
}