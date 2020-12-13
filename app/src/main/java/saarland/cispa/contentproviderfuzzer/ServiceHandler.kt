package saarland.cispa.contentproviderfuzzer

import android.app.Service
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log


class ServiceHandler(
    private val service: Service,
    looper: Looper,
    contentProviderFuzzer: ContentProviderFuzzer,
    private val handlerThread: HandlerThread
) : Handler(looper) {

    companion object {
        private const val TAG = "ServiceHandler"
    }

    private val workerConnection = WorkerConnection(contentProviderFuzzer)

    override fun handleMessage(msg: Message) {
        val serverPort = msg.arg1
        workerConnection.messageLoop(serverPort)

        Log.v(TAG, "Finished fuzzing")

        // Stop thread and service
        handlerThread.quitSafely()
        service.stopSelf()
    }
}
