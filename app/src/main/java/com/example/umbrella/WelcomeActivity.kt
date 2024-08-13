package com.example.umbrella

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var logoutButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Set welcome message
        welcomeTextView.text = "Welcome to UmRella!"

        // Set click listener for logout button
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Navigate back to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finish this activity to remove it from the back stack
    }
}
