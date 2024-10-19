package com.example.umbrella.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.umbrella.R
import com.example.umbrella.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var currentUser: FirebaseUser

    private lateinit var profileName: EditText
    private lateinit var profilePhone: EditText
    private lateinit var profileAddress: EditText
    private lateinit var profileBirthday: EditText
    private lateinit var profileEmail: TextView

    private val calendar = Calendar.getInstance() // Calendar instance to hold selected date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("users")

        profileName = view.findViewById(R.id.profile_name)
        profilePhone = view.findViewById(R.id.profile_phone)
        profileAddress = view.findViewById(R.id.profile_address)
        profileBirthday = view.findViewById(R.id.profile_birthday)
        profileEmail = view.findViewById(R.id.profile_email)

        // Set the email from FirebaseAuth
        profileEmail.text = currentUser.email

        // Date Picker for Birthday
        profileBirthday.setOnClickListener {
            showDatePickerDialog()
        }

        // Fetch and populate data
        loadProfileData()

        // Save button functionality
        val saveButton: Button = view.findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            saveProfileData()
        }

        // Logout button functionality
        val logoutButton: Button = view.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(activity, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        return view
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateBirthdayField()
        }

        // Show DatePickerDialog with current date
        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateBirthdayField() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        profileBirthday.setText(format.format(calendar.time)) // Set selected date in EditText
    }

    private fun loadProfileData() {
        val userRef = database.child(currentUser.uid)
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.child("name").value.toString()
                val phone = snapshot.child("phone").value.toString()
                val address = snapshot.child("address").value.toString()
                val birthday = snapshot.child("birthday").value.toString()

                profileName.setText(name)
                profilePhone.setText(phone)
                profileAddress.setText(address)
                profileBirthday.setText(birthday)
            } else {
                Toast.makeText(activity, "No profile data found. Please fill in the details.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to load profile data.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileData() {
        val name = profileName.text.toString()
        val phone = profilePhone.text.toString()
        val address = profileAddress.text.toString()
        val birthday = profileBirthday.text.toString()

        val userProfile = mapOf(
            "name" to name,
            "phone" to phone,
            "address" to address,
            "birthday" to birthday
        )

        database.child(currentUser.uid).setValue(userProfile).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(activity, "Profile data saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed to save profile data.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
