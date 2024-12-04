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
import com.example.sem.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firstNameText: EditText
    private lateinit var lastNameText: EditText
    private lateinit var emailText: EditText
    private lateinit var graduationText: EditText
    private lateinit var passwordText: EditText
    private lateinit var retypePasswordText: EditText
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        firstNameText = findViewById(R.id.et_firstName)
        lastNameText = findViewById(R.id.et_lastName)
        graduationText = findViewById(R.id.et_graduation)
        emailText = findViewById(R.id.et_email)
        passwordText = findViewById(R.id.et_password)
        retypePasswordText = findViewById(R.id.et_repassword)

        val registerButton = findViewById<Button>(R.id.btn_register)
        val login = findViewById<TextView>(R.id.tv_login)

        // A click listener for the login button
        registerButton.setOnClickListener {
            registerUser()
        }

        // Set click listener for sign up text
        login.setOnClickListener {
            // Navigate to sign up activity (you'll need to create this)
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser() {
        // Trimming to remove empty spaces
        val firstName = firstNameText.text.toString().trim()
        val lastName = lastNameText.text.toString().trim()
        val graduation = graduationText.text.toString().trim()
        val email = emailText.text.toString().trim()
        val password1 = passwordText.text.toString().trim()
        val password2 = retypePasswordText.text.toString().trim()

        // Checking for empty fields
        if (firstName.isEmpty()) {
            firstNameText.error = "Email is required"
            firstNameText.requestFocus()
            return
        }

        if (lastName.isEmpty()) {
            lastNameText.error = "Email is required"
            lastNameText.requestFocus()
            return
        }

        if (graduation.isEmpty()) {
            graduationText.error = "Email is required"
            graduationText.requestFocus()
            return
        }

        if (email.isEmpty()) {
            emailText.error = "Email is required"
            emailText.requestFocus()
            return
        }

        if (password1.isEmpty()) {
            passwordText.error = "Password is required"
            passwordText.requestFocus()
            return
        }

        if (password2.isEmpty()) {
            retypePasswordText.error = "Password is required"
            retypePasswordText.requestFocus()
            return
        }

        //Checking if the given passwords are equal
        if(password1 != password2) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            return
        }

        // Attempt login
        firebaseAuth.createUserWithEmailAndPassword(email, password1)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // sign up is success, navigate to main activity
                    val user = User(firstName, lastName, email, yearGraduation = graduation)

                    firebaseAuth.currentUser?.let { createUser(it.uid, user) }

                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    finish()
                } else {
                    // If sign up fails, display a message to the user
                    Toast.makeText(this, "Sign in failed. Please try again.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun createUser(uid: String, user: User) {
        // Create Database instance
        db = FirebaseFirestore.getInstance()

        //Creating a user into the firestore database
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: $documentReference")
                Toast.makeText(this, "Welcome" ,Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }
}