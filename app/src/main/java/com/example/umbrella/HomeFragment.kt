package com.example.umbrella

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var welcomeTextView: TextView
    private lateinit var currentTemperature: TextView
    private lateinit var currentCondition: TextView

    // Replace this with your OpenWeatherMap API key
    private val apiKey = "bd5e378503939ddaee76f12ad7a97608"
    private val city = "Coimbatore"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        welcomeTextView = view.findViewById(R.id.welcomeTextView)
        currentTemperature = view.findViewById(R.id.currentTemperature)
        currentCondition = view.findViewById(R.id.currentCondition)

        // Set welcome message
        welcomeTextView.text = "Welcome to UmRella!"

        // Fetch real-time weather data
        fetchWeatherData()
        val bookUmbrellaButton: MaterialButton = view.findViewById(R.id.bookUmbrellaButton)
        bookUmbrellaButton.setOnClickListener {
            // Replace the current fragment with BookUmbrellaFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, BookUmbrellaFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun fetchWeatherData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/") // OpenWeatherMap API base URL
            .addConverterFactory(GsonConverterFactory.create()) // GSON converter to parse the response
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)
        val call = weatherService.getWeather(city, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    weather?.let {
                        // Update the UI with the fetched weather data
                        currentTemperature.text = "${it.main.temp}°C" // Display temperature
                        currentCondition.text = it.weather[0].description.capitalize() // Capitalize the first letter of description
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