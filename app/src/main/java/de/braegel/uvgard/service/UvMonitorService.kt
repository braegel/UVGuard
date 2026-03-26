package de.braegel.uvgard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import de.braegel.uvgard.data.location.AndroidLocationSource
import de.braegel.uvgard.data.location.LocationPoller
import de.braegel.uvgard.data.model.UvData
import de.braegel.uvgard.data.repository.OkHttpUvClient
import de.braegel.uvgard.data.repository.UvRepository
import de.braegel.uvgard.notification.ThresholdEvent
import de.braegel.uvgard.notification.UvThresholdChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UvMonitorService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val thresholdChecker = UvThresholdChecker()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        startForeground(1, buildNotification())
        startMonitoring()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun startMonitoring() {
        val locationSource = AndroidLocationSource(this)
        val locationPoller = LocationPoller(locationSource, intervalMs = 10 * 60 * 1000L)
        val uvRepository = UvRepository(OkHttpUvClient())

        serviceScope.launch {
            locationPoller.locationFlow().collect { location ->
                val results = uvRepository.fetchAll(location.latitude, location.longitude)
                if (results.isNotEmpty()) {
                    UvMonitorState.updateUvData(results)
                    checkThreshold(results)
                }
            }
        }
    }

    private fun checkThreshold(uvDataList: List<UvData>) {
        val event = thresholdChecker.check(uvDataList, UvMonitorState.threshold.value)
        val maxUv = uvDataList.maxOf { it.currentUvIndex }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (event) {
            is ThresholdEvent.Exceeded -> {
                manager.notify(ALERT_NOTIFICATION_ID, buildAlertNotification(
                    title = "UV Index Alert",
                    text = "UV index is %.1f — exceeds threshold of %d".format(maxUv, UvMonitorState.threshold.value.toInt()),
                    icon = android.R.drawable.ic_dialog_alert
                ))
                vibrate()
            }
            is ThresholdEvent.Safe -> {
                manager.notify(ALERT_NOTIFICATION_ID, buildAlertNotification(
                    title = "UV Index Safe",
                    text = "UV index dropped to %.1f — below threshold".format(maxUv),
                    icon = android.R.drawable.ic_dialog_info
                ))
            }
            is ThresholdEvent.NoChange -> {}
        }
    }

    private fun buildAlertNotification(title: String, text: String, icon: Int): Notification {
        return Notification.Builder(this, ALERT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(icon)
            .build()
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "UV Monitor", NotificationManager.IMPORTANCE_LOW)
        )
        manager.createNotificationChannel(
            NotificationChannel(ALERT_CHANNEL_ID, "UV Alerts", NotificationManager.IMPORTANCE_HIGH)
        )
    }

    private fun buildNotification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("UVGuard")
            .setContentText("Monitoring UV index")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "uv_monitor"
        const val ALERT_CHANNEL_ID = "uv_alert"
        const val ALERT_NOTIFICATION_ID = 2
    }
}
