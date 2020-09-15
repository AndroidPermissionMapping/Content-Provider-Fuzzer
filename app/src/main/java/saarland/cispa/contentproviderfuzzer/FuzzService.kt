package saarland.cispa.contentproviderfuzzer

import android.app.Service
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log


class FuzzService : Service() {

    companion object {
        private const val TAG = "FuzzService"
    }

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "Creating service")
        HandlerThread("FuzzingThread").apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper

            val inputFileParser = InputAndResultsIO(this@FuzzService)
            val resolverCaller = ResolverCaller(contentResolver)
            serviceHandler = ServiceHandler(looper, inputFileParser, resolverCaller)
        }

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