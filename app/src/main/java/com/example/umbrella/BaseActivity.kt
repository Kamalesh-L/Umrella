package com.example.umbrella

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.umbrella.auth.LoginActivity
import com.example.umbrella.fragment.BookingFragment
import com.example.umbrella.fragment.HomeFragment
import com.example.umbrella.fragment.ProfileFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // Use your menu icon

        // Set up navigation view
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Set default fragment
        if (savedInstanceState == null) {
            checkUserProfile()
        }
        val userEmail = getUserEmail()
        val navHeaderView = navigationView.getHeaderView(0)
        val emailTextView = navHeaderView.findViewById<TextView>(R.id.nav_header_subtitle)
        emailTextView.text = userEmail
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(HomeFragment())
            }
            R.id.nav_profile -> {
                replaceFragment(ProfileFragment())
            }
            R.id.nav_booking -> {
                replaceFragment(BookingFragment())
            }
            R.id.nav_logout -> {
                logout()
            }
            R.id.nav_pred -> {
                replaceFragment(predict())
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Navigate back to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finish this activity to remove it from the back stack
    }
    private fun getUserEmail(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.email
    }
    private fun checkUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)
            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Profile data exists, navigate to HomeFragment
                    replaceFragment(HomeFragment())
                } else {
                    // Profile data does not exist, navigate to ProfileFragment
                    replaceFragment(ProfileFragment())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to check user profile.", Toast.LENGTH_SHORT).show()
                // Default to ProfileFragment in case of failure
                replaceFragment(ProfileFragment())
            }
        } else {
            // No user is logged in, navigate to ProfileFragment
            replaceFragment(ProfileFragment())
        }
    }
}