package com.example.umbrella

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class predict : Fragment() {

    private lateinit var resultTextView: TextView
    private lateinit var weatherDetailsTextView: TextView
    private lateinit var predictButton: Button
    private lateinit var interpreter: Interpreter

    // Replace these with your actual min and max values from your dataset
    private val minTemp = 15f
    private val maxTemp = 40f
    private val minPressure = 980f
    private val maxPressure = 1050f
    private val minHumidity = 20f
    private val maxHumidity = 100f
    private val minWindSpeed = 0f
    private val maxWindSpeed = 20f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_predict, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultTextView = view.findViewById(R.id.resultTextView)
        weatherDetailsTextView = view.findViewById(R.id.weatherDetailsTextView) // Initialize new TextView
        predictButton = view.findViewById(R.id.predictButton)

        // Load the TensorFlow Lite model
        interpreter = Interpreter(requireContext().loadModelFile("logistic_regression_model (1).tflite"))

        predictButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                fetchWeatherData()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun fetchWeatherData() {
        val city = "Coimbatore"
        val apiKey = "bd5e378503939ddaee76f12ad7a97608"

        val weatherData = getWeatherData(city, apiKey)

        withContext(Dispatchers.Main) {
            weatherData?.let { data ->
                val tempMin = data.main.tempMin
                val tempMax = data.main.tempMax
                val pressure = data.main.pressure
                val humidity = data.main.humidity
                val windSpeed = data.wind.speed
                weatherDetailsTextView.text = "Current Weather Details:\n" +
                        "Temp Min: $tempMin °C\n" +
                        "Temp Max: $tempMax °C\n" +
                        "Pressure: $pressure hPa\n" +
                        "Humidity: $humidity%\n" +
                        "Wind Speed: $windSpeed m/s"
                // Scale the values
                val tempMinScaled = (tempMin - minTemp) / (maxTemp - minTemp)
                val tempMaxScaled = (tempMax - minTemp) / (maxTemp - minTemp)
                val pressureScaled = (pressure - minPressure) / (maxPressure - minPressure)
                val humidityScaled = (humidity - minHumidity) / (maxHumidity - minHumidity)
                val windSpeedScaled = (windSpeed - minWindSpeed) / (maxWindSpeed - minWindSpeed)

                // Prepare input for the model
                val input = arrayOf(floatArrayOf(tempMinScaled, tempMaxScaled, pressureScaled, humidityScaled, windSpeedScaled))
                val output = Array(1) { FloatArray(1) }

                // Make the prediction
                interpreter.run(input, output)

                // Display the result
                val prediction = output[0][0]
                resultTextView.text = "Prediction: ${if (prediction > 0.26) "Chance of Rain: Yes☔️" else "chance of Rain : No🌤️"}"
            } ?: run {
                Toast.makeText(requireContext(), "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getWeatherData(city: String, apiKey: String): WeatherResponse? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric")
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        Gson().fromJson(it, WeatherResponse::class.java)
                    }
                } else {
                    null
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interpreter.close() // Close the interpreter when done
    }

    // Data classes for JSON response parsing
    data class WeatherResponse(
        val main: Main,
        val wind: Wind
    )

    data class Main(
        @SerializedName("temp_min")
        val tempMin: Float,
        @SerializedName("temp_max")
        val tempMax: Float,
        val pressure: Float,
        val humidity: Float
    )

    data class Wind(
        val speed: Float
    )
}

fun Context.loadModelFile(modelPath: String): ByteBuffer {
    // Open a file descriptor for the model file in the assets folder
    val assetFileDescriptor = assets.openFd(modelPath)

    // Create a FileInputStream from the file descriptor
    val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
    val fileChannel: FileChannel = inputStream.channel

    // Map the channel to a ByteBuffer
    val startOffset = assetFileDescriptor.startOffset
    val declaredLength = assetFileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).also {
        // Close the input stream after mapping to avoid resource leaks
        inputStream.close()
    }
}
