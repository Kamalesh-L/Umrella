package com.example.umbrella.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.umbrella.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.integration.android.IntentIntegrator
import androidx.activity.result.contract.ActivityResultContracts

class BookUmbrellaFragment : Fragment() {

    private lateinit var resultTextView: TextView
    private lateinit var scanButton: Button
    private lateinit var umbrellaScanButton: Button
    private var isStationValid = false

    private val validUmbrellaIds = listOf(
        "umbrella-1", "umbrella-2", "umbrella-3",
        "umbrella-4", "umbrella-5", "umbrella-6",
        "umbrella-7", "umbrella-8", "umbrella-9", "umbrella-10"
    )

    private val qrScanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            if (intentResult != null) {
                if (intentResult.contents != null) {
                    val scannedCode = intentResult.contents
                    if (isStationValid) {
                        validateUmbrella(scannedCode)
                    } else {
                        validateStation(scannedCode)
                    }
                } else {
                    Toast.makeText(activity, "No QR code found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_umbrella, container, false)

        // Initialize views
        resultTextView = view.findViewById(R.id.resultTextView)
        scanButton = view.findViewById(R.id.scanButton)
        umbrellaScanButton = view.findViewById(R.id.umbrellaScanButton)

        scanButton.setOnClickListener {
            startQrScanner()
        }

        umbrellaScanButton.setOnClickListener {
            startQrScanner()
        }
        umbrellaScanButton.isEnabled = false

        return view
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(true)
        integrator.setPrompt("Scan a QR Code")
        integrator.setBeepEnabled(true)
        qrScanLauncher.launch(integrator.createScanIntent())
    }

    private fun validateStation(scannedCode: String) {
        when (scannedCode) {
            "station-1", "station-2" -> {
                isStationValid = true
                resultTextView.text = "Valid Station: $scannedCode. Now scan an umbrella QR Code."
                umbrellaScanButton.isEnabled = true
            }
            else -> {
                resultTextView.text = "Invalid Station QR Code"
                isStationValid = false
            }
        }
    }

    private fun validateUmbrella(scannedCode: String) {
        if (validUmbrellaIds.contains(scannedCode)) {
            resultTextView.text = "Umbrella Booked: $scannedCode"
            bookUmbrella(scannedCode)
        } else {
            resultTextView.text = "Invalid Umbrella QR Code"
        }
    }

    private fun bookUmbrella(umbrellaId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val database = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}")
            database.child("bookedUmbrella").setValue(true)
            database.child("bookedUmbrellaId").setValue(umbrellaId)

            val umbrellaRef = FirebaseDatabase.getInstance().getReference("umbrellas/$umbrellaId")
            umbrellaRef.child("status").setValue("not available")

            // Update available umbrellas in the station
            val stationRef = FirebaseDatabase.getInstance().getReference("stations/station-1")
            stationRef.child("availableUmbrellas").get().addOnSuccessListener {
                val availableUmbrellas = it.value as Long
                stationRef.child("availableUmbrellas").setValue(availableUmbrellas - 1)
            }

            Toast.makeText(activity, "Umbrella $umbrellaId booked!", Toast.LENGTH_SHORT).show()
            // After booking, disable the umbrella scan button
            umbrellaScanButton.isEnabled = false
        } else {
            Toast.makeText(activity, "Please log in to book an umbrella.", Toast.LENGTH_SHORT).show()
        }
    }
}