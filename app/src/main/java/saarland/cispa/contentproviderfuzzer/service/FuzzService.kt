package saarland.cispa.contentproviderfuzzer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log
import saarland.cispa.contentproviderfuzzer.FuzzingOrchestrator
import saarland.cispa.contentproviderfuzzer.ResolverCaller


class FuzzService : Service() {

    companion object {
        private const val TAG = "FuzzService"

        private const val NOTIFICATION_CHANNEL_ID = "Default"
        private const val ONGOING_NOTIFICATION_ID: Int = 57983245

        private const val BUNDLE_KEY_SERVER_PORT = "server_port"
    }

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "Creating service")

        startAsForegroundService()

        HandlerThread("FuzzingThread").apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper

            val resolverCaller = ResolverCaller(contentResolver)
            val orchestrator = FuzzingOrchestrator(resolverCaller)

            serviceHandler = ServiceHandler(
                this@FuzzService,
                looper, this, orchestrator
            )
        }

        Log.v(TAG, "End onCreate()")
    }

    private fun startAsForegroundService() {
        // Setup notification channel
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Default",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification: Notification =
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID).build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "SS onStartCommand()")

        if (intent != null) {
            Log.v(TAG, intent.toString())
        }

        if (intent != null && intent.hasExtra(BUNDLE_KEY_SERVER_PORT)) {
            val serverPort = intent.getIntExtra(BUNDLE_KEY_SERVER_PORT, 0)

            Log.v(TAG, "Got server port $serverPort")

            serviceHandler?.obtainMessage()?.also { msg ->
                msg.arg1 = serverPort

                serviceHandler?.sendMessage(msg)
            }
        }

        Log.v(TAG, "EE onStartCommand()")


        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "Destroying service")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}