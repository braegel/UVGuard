package de.braegel.uvgard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import de.braegel.uvgard.service.UvMonitorService
import de.braegel.uvgard.service.UvMonitorState
import de.braegel.uvgard.ui.UvIndexScreen
import de.braegel.uvgard.ui.theme.UVGuardTheme

class MainActivity : ComponentActivity() {

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            requestBackgroundLocationOrStart()
        }
    }

    private val backgroundLocationRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startMonitorService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UVGuardTheme {
                val uvDataList by UvMonitorState.uvData.collectAsState()
                val threshold by UvMonitorState.threshold.collectAsState()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UvIndexScreen(
                        uvDataList = uvDataList,
                        threshold = threshold,
                        onThresholdChanged = { UvMonitorState.setThreshold(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        if (hasLocationPermission()) {
            requestBackgroundLocationOrStart()
        } else {
            val permissions = mutableListOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            locationPermissionRequest.launch(permissions.toTypedArray())
        }
    }

    private fun requestBackgroundLocationOrStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            backgroundLocationRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            startMonitorService()
        }
    }

    private fun startMonitorService() {
        val intent = Intent(this, UvMonitorService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
