package com.example.umbrella

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var redirectToLogin: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Ensure this is the correct layout file

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.confirmPassword)
        registerButton = findViewById(R.id.registerButton)
        redirectToLogin = findViewById(R.id.redirectToLogin)

        // Register button click listener
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                // Create user
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful, redirect to WelcomeActivity
                            startActivity(Intent(this, WelcomeActivity::class.java))
                            finish()
                        } else {
                            // Registration failed, show a message to the user
                            // Handle error here (e.g., showToast or AlertDialog)
                        }
                    }
            } else {
                // Handle input errors (e.g., showToast or AlertDialog)
            }
        }

        // Redirect to login on click
        redirectToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish this activity if you don't want to return to it
        }
    }
}
