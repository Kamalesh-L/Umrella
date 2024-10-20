package com.example.umbrella.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.umbrella.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class BookUmbrellaFragment : Fragment() {

    private lateinit var resultTextView: TextView
    private lateinit var scanButton: Button
    private lateinit var umbrellaScanButton: Button

    private val validUmbrellaIds = listOf("umbrella-1", "umbrella-2", "umbrella-3", "umbrella-4", "umbrella-5",
        "umbrella-6", "umbrella-7", "umbrella-8", "umbrella-9", "umbrella-10",
        "umbrella-11", "umbrella-12", "umbrella-13", "umbrella-14", "umbrella-15",
        "umbrella-16", "umbrella-17", "umbrella-18", "umbrella-19", "umbrella-20")

    private var isStationValid = false

    // Register for activity result using the new API
    private val qrScanLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            if (intentResult != null) {
                if (intentResult.contents == null) {
                    Toast.makeText(activity, "Scan Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    // Handle the scanned QR code
                    if (!isStationValid) {
                        validateStation(intentResult.contents)
                    } else {
                        validateUmbrella(intentResult.contents)
                    }
                }
            } else {
                Toast.makeText(activity, "No scan data received", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_book_umbrella, container, false)

        // Initialize views
        resultTextView = view.findViewById(R.id.resultTextView)
        scanButton = view.findViewById(R.id.scanButton)
        umbrellaScanButton = view.findViewById(R.id.umbrellaScanButton)

        // Set click listener for the station QR scan button
        scanButton.setOnClickListener {
            startQrScanner()
        }

        // Set click listener for umbrella QR scan button (Initially disabled)
        umbrellaScanButton.setOnClickListener {
            startQrScanner()
        }
        umbrellaScanButton.isEnabled = false // Initially disabled

        return view
    }

    // Method to start QR Scanner
    private fun startQrScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(true) // Lock orientation to portrait
        integrator.setPrompt("Scan a QR Code") // Set a prompt for the scanner
        integrator.setBeepEnabled(true) // Enable beep sound after scan
        qrScanLauncher.launch(integrator.createScanIntent()) // Start the QR scanner with the new launcher
    }

    // Method to validate the scanned QR code for a station
    private fun validateStation(scannedCode: String) {
        when (scannedCode) {
            "station-1", "station-2" -> {
                isStationValid = true
                resultTextView.text = "Valid QR Code: $scannedCode. Now scan an umbrella QR Code."
                Toast.makeText(activity, "Valid Station: $scannedCode", Toast.LENGTH_SHORT).show()
                umbrellaScanButton.isEnabled = true // Enable umbrella scan button
            }
            else -> {
                resultTextView.text = "Invalid QR Code"
                Toast.makeText(activity, "Invalid QR Code", Toast.LENGTH_SHORT).show()
                isStationValid = false
            }
        }
    }

    // Method to validate the scanned umbrella QR code
    private fun validateUmbrella(scannedCode: String) {
        if (validUmbrellaIds.contains(scannedCode)) {
            resultTextView.text = "Umbrella Booked: $scannedCode"
            Toast.makeText(activity, "Umbrella Booked: $scannedCode", Toast.LENGTH_SHORT).show()
            // Reset state after booking
            isStationValid = false
            umbrellaScanButton.isEnabled = false // Disable umbrella scan button
        } else {
            resultTextView.text = "Invalid Umbrella QR Code"
            Toast.makeText(activity, "Invalid Umbrella QR Code", Toast.LENGTH_SHORT).show()
        }
    }
}
