package com.example.qrscanapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qrscanapp.databinding.ActivityMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {
    private val requiredPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Boolean
            if (isGranted) {
                // Permission is granted
                showCamera()
            } else {
                // Permission is denied
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()) {
            result: ScanIntentResult -> run {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                setResult(result.contents)
            }
        }
    }

    private lateinit var binding: ActivityMainBinding

    private fun setResult(string: String) {
        binding.tvResult.text = string
    }

    private fun showCamera() {
        var options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Place a QR code inside the viewfinder rectangle to scan it.")
        options.setBeepEnabled(false)
        options.setCameraId(0)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initViews()
    }

    private fun initViews() {
        binding.fab.setOnClickListener {
            checkPermissionCamera(this)
        }
    }

    private fun checkPermissionCamera(context : MainActivity) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Toast.makeText(context, "Camera permission is required to scan QR code", Toast.LENGTH_SHORT).show()
        } else {
            // No explanation needed, we can request the permission.
            requiredPermission.launch(android.Manifest.permission.CAMERA)

        }
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}