package com.example.qrscanapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    val uri = Uri.parse(result.contents)
                    if (uri.scheme != null) {
                        if (uri.scheme.equals("http", ignoreCase = true) || uri.scheme.equals(
                                "https",
                                ignoreCase = true
                            )
                        ) {
                            startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } else if (uri.host != null && uri.host.equals(
                                "youtube.com",
                                ignoreCase = true
                            ) || uri.host.equals("youtu.be", ignoreCase = true)
                        ) {
                            openYouTubeApp(uri)
                        }
                    } else {
                        setResult(result.contents)
                    }
                }
            }
        }

    private fun openYouTubeApp(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.youtube")
        try {
            startActivity(intent)
        } catch (ex: Exception) {
            // If YouTube app is not installed, open the link in the default web browser
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private lateinit var binding: ActivityMainBinding

    private fun setResult(string: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("QR Result", string)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showCamera() {
        val options = ScanOptions()
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

    private fun checkPermissionCamera(context: MainActivity) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
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