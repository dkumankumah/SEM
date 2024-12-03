package com.example.sem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.sem.model.Event
import com.example.sem.model.SignUpActivity
import com.example.sem.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Create Database instance
        db = FirebaseFirestore.getInstance()

        // Initialize views
        emailText = findViewById(R.id.et_email)
        passwordText = findViewById(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val signUp = findViewById<TextView>(R.id.tv_sign_up)
        role = "";

        // A click listener for the login button
        loginButton.setOnClickListener {
            loginUser()
        }

        // Set click listener for sign up text
        signUp.setOnClickListener {
            // Navigate to sign up activity (you'll need to create this)
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun loginUser() {
        // Trimming to remove empty spaces
        val email = emailText.text.toString().trim()
        val password = passwordText.text.toString().trim()

        // Checking for empty fields in email and password
        if (email.isEmpty()) {
            emailText.error = "Email is required"
            emailText.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordText.error = "Password is required"
            passwordText.requestFocus()
            return
        }

        // Attempt login
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val userId = firebaseAuth.currentUser?.uid

                    //Fething user data from the firestore database
                    if (userId != null) {
                       db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val user = document.toObject(User::class.java)!!
                                    role = user.role

                                    // Login success, navigate to Dashboard activity
                                    val intent = Intent(this, AdminDashboardActivity::class.java)
                                    intent.putExtra("USER_ROLE", role) // If you need to pass user data
                                    startActivity(intent)
                                    finish()

                                }                            }


                    }


                } else {
                    // If login fails, display a message to the user
                    Toast.makeText(baseContext, "Login failed. Please try again.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }



}