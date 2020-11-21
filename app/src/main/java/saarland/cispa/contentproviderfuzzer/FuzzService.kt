package saarland.cispa.contentproviderfuzzer

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


class FuzzService : Service() {

    companion object {
        private const val TAG = "FuzzService"

        private const val NOTIFICATION_CHANNEL_ID = "Default"
        private const val ONGOING_NOTIFICATION_ID: Int = 57983245
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

            val inputFileParser = InputAndResultsIO(this@FuzzService)
            val resolverCaller = ResolverCaller(contentResolver)
            serviceHandler = ServiceHandler(
                this@FuzzService,
                looper, inputFileParser, resolverCaller
            )
        }

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
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "Destroying service")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}