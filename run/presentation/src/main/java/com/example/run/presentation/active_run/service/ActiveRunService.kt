package com.example.run.presentation.active_run.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.example.core.presentation.ui.formatted
import com.example.run.domain.RunningTracker
import com.example.run.presentation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class ActiveRunService: Service() {

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private val baseNotification by lazy {
        NotificationCompat.Builder(applicationContext, ACTIVE_RUN_CHANNEL)
            .setSmallIcon(com.example.core.presentation.designsystem.R.drawable.logo)
            .setContentTitle(getString(R.string.active_run))
    }

    private val runningTracker by inject<RunningTracker>()

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_CLASS) ?: throw IllegalArgumentException("Activity class not found")
                start(Class.forName(activityClass))
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start(activityClass: Class<*>) {
        if (!isServiceActive) {
            isServiceActive = true
            createNotificationChannel(ACTIVE_RUN_CHANNEL, getString(R.string.active_run), NotificationManager.IMPORTANCE_DEFAULT)

            // Deep link to ActiveRun screen
            val activityIntent = Intent(applicationContext, activityClass).apply {
                data = "runmate://active_run".toUri()
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }
            val notification = baseNotification
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent)
                .build()

            startForeground(1, notification) // Don't use 0 for id
            updateNotification()
        }
    }

    private fun stop() {
        stopSelf()
        isServiceActive = false
        serviceScope.cancel()

        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    private fun createNotificationChannel(channelId: String, name: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                name,
                importance
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        runningTracker.elapsedTime.onEach { elapsedTime ->
            val notification =  baseNotification
                .setContentText(elapsedTime.formatted())
                .build()
            notificationManager.notify(1, notification)
        }.launchIn(serviceScope)
    }

    companion object {
        const val ACTIVE_RUN_CHANNEL = "active_run_channel"

        var isServiceActive = false

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"

        private const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"

        fun createStartIntent(context: Context, activityClass: Class<*>): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}