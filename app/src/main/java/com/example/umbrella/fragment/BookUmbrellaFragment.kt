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
import androidx.fragment.app.Fragment
import com.example.umbrella.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class BookUmbrellaFragment : Fragment() {

    private lateinit var resultTextView: TextView
    private lateinit var scanButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_book_umbrella, container, false)

        // Initialize views
        resultTextView = view.findViewById(R.id.resultTextView)
        scanButton = view.findViewById(R.id.scanButton)

        // Set click listener for the QR scan button
        scanButton.setOnClickListener {
            startQrScanner()
        }

        return view
    }

    // Method to start QR Scanner
    private fun startQrScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(true) // Lock orientation to portrait
        integrator.setPrompt("Scan a QR Code") // Set a prompt for the scanner
        integrator.setBeepEnabled(true) // Enable beep sound after scan
        integrator.initiateScan() // Start the QR scanner
    }

    // Handle QR scan result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Scan Cancelled", Toast.LENGTH_LONG).show()
            } else {
                // Handle the scanned QR code
                validateStation(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // Method to validate the scanned QR code for a station
    private fun validateStation(scannedCode: String) {
        when (scannedCode) {
            "station-1", "station-2" -> {
                resultTextView.text = "Valid QR Code: $scannedCode"
                Toast.makeText(activity, "Valid Station: $scannedCode", Toast.LENGTH_SHORT).show()
            }
            else -> {
                resultTextView.text = "Invalid QR Code"
                Toast.makeText(activity, "Invalid QR Code", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
