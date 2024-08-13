package com.example.umbrella

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        if (auth.currentUser != null) {
            // User is signed in, navigate to WelcomeActivity
            startActivity(Intent(this, WelcomeActivity::class.java))
        } else {
            // No user is signed in, navigate to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        // Close this activity to prevent navigating back to it
        finish()
    }
}
