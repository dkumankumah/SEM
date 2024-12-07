package com.example.sem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sem.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: User
    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        // View initialization
//        val username = findViewById<TextView>(R.id.tv_fullName)
//        val email = findViewById<TextView>(R.id.tv_email)
//        val classof = findViewById<TextView>(R.id.tv_classOf)
//        val role = findViewById<TextView>(R.id.tv_role)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        logoutBtn = findViewById(R.id.btn_logout)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Create Database instance
        db = FirebaseFirestore.getInstance()

        // Set the initial selected item
        bottomNav.setSelectedItemId(R.id.nav_account)

        // Fetching data profile
        fetchUser(firebaseAuth.currentUser!!.uid)

        // Navigation bar to all other activities
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    // Navigate to AccountActivity
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
                    val intent = Intent(this, MapHostActivity::class.java)
                    intent.putExtra("ALL_EVENTS", true)
                    startActivity(intent)
                    true
                }
                R.id.nav_account -> {
                    true
                }
                else -> false
            }
        }

        logoutBtn.setOnClickListener {
            firebaseAuth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUser(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    user = document.toObject(User::class.java)!!
                    populateViews() // Call the method to populate views here
                }
            }

    }

    private fun populateViews() {
        val firstName = user.firstName
        val lastName = user.lastName
        val fullName = "$firstName $lastName"
        val username = findViewById<TextView>(R.id.tv_fullName)
        val email = findViewById<TextView>(R.id.tv_email)
        val classof = findViewById<TextView>(R.id.tv_classOf)
        val role = findViewById<TextView>(R.id.tv_role)
        val image = findViewById<ImageView>(R.id.imageView2)

        username.text = fullName
        email.text = user.email
        classof.text = user.yearGraduation
        role.text = user.role
    }
}