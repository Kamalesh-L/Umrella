package com.example.umbrella.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.umbrella.R
import com.example.umbrella.WeatherResponse
import com.example.umbrella.WeatherService
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var welcomeTextView: TextView
    private lateinit var currentTemperature: TextView
    private lateinit var currentCondition: TextView
    private lateinit var bookUmbrellaButton: MaterialButton
    private lateinit var dropUmbrellaButton: MaterialButton
    private lateinit var viewMapButton: MaterialButton

    // Replace this with your OpenWeatherMap API key
    private val apiKey = "bd5e378503939ddaee76f12ad7a97608"
    private val city = "Coimbatore"

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        welcomeTextView = view.findViewById(R.id.welcomeTextView)
        currentTemperature = view.findViewById(R.id.currentTemperature)
        currentCondition = view.findViewById(R.id.currentCondition)
        bookUmbrellaButton = view.findViewById(R.id.bookUmbrellaButton)
        dropUmbrellaButton = view.findViewById(R.id.dropUmbrellaButton)
        viewMapButton = view.findViewById(R.id.viewMapButton)

        // Set welcome message
        welcomeTextView.text = "Welcome to UmRella!"

        // Fetch real-time weather data
        fetchWeatherData()

        // Check if the user has booked an umbrella
        checkUmbrellaStatus()

        // Book Umbrella button action
        bookUmbrellaButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, BookUmbrellaFragment())
                .addToBackStack(null)
                .commit()
        }

        // Drop Umbrella button action
        dropUmbrellaButton.setOnClickListener {
            dropUmbrella()
        }

        // View Map button action
        viewMapButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, MapFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun checkUmbrellaStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}")
            userRef.child("bookedUmbrella").get().addOnSuccessListener {
                val isBooked = it.value as Boolean
                if (isBooked) {
                    bookUmbrellaButton.isEnabled = false
                    dropUmbrellaButton.isEnabled = true
                } else {
                    bookUmbrellaButton.isEnabled = true
                    dropUmbrellaButton.isEnabled = false
                }
            }
        }
    }

    private fun dropUmbrella() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}")
            userRef.child("bookedUmbrella").setValue(false)
            userRef.child("bookedUmbrellaId").get().addOnSuccessListener {
                val umbrellaId = it.value.toString()
                val umbrellaRef = FirebaseDatabase.getInstance().getReference("umbrellas/$umbrellaId")
                umbrellaRef.child("status").setValue("available")

                // Update available umbrellas in the station
                val stationRef = FirebaseDatabase.getInstance().getReference("stations/station-1")
                stationRef.child("availableUmbrellas").get().addOnSuccessListener {
                    val availableUmbrellas = it.value as Long
                    stationRef.child("availableUmbrellas").setValue(availableUmbrellas + 1)
                }
                // Reset button states
                bookUmbrellaButton.isEnabled = true
                dropUmbrellaButton.isEnabled = false
                Toast.makeText(activity, "Umbrella dropped!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeatherData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)
        val call = weatherService.getWeather(city, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    weather?.let {
                        // Update the UI with the fetched weather data
                        currentTemperature.text = "${it.main.temp}°C"
                        currentCondition.text = it.weather[0].description.capitalize()
                    }
                } else {
                    showToast("Error fetching weather data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                showToast("Failed to fetch weather data: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
