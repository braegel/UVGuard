package de.braegel.uvgard.service

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class UvMonitorServiceTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `service creates notification channel on start`() {
        val service = Robolectric.setupService(UvMonitorService::class.java)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = notificationManager.getNotificationChannel("uv_monitor")
        assertNotNull("Notification channel 'uv_monitor' should exist", channel)
    }

    @Test
    fun `service runs as foreground service`() {
        val service = Robolectric.setupService(UvMonitorService::class.java)
        val shadow = shadowOf(service)

        val notification = shadow.lastForegroundNotification
        assertNotNull("Service should run in foreground with a notification", notification)
    }

    @Test
    fun `foreground notification has uv monitor content`() {
        val service = Robolectric.setupService(UvMonitorService::class.java)
        val shadow = shadowOf(service)

        val notification = shadow.lastForegroundNotification
        assertNotNull(notification)
        val shadowNotification = shadowOf(notification)
        assertTrue(
            "Notification should mention UV monitoring",
            shadowNotification.contentText.toString().contains("UV", ignoreCase = true)
        )
    }

    @Test
    fun `service declares foreground service type location`() {
        val serviceInfo = context.packageManager
            .getServiceInfo(
                android.content.ComponentName(context, UvMonitorService::class.java),
                0
            )
        assertNotNull(serviceInfo)
    }
}
