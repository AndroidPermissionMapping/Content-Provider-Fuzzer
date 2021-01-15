package saarland.cispa.contentproviderfuzzer.service

import android.app.Service
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import saarland.cispa.contentproviderfuzzer.FuzzingOrchestrator


class ServiceHandler(
    private val service: Service,
    looper: Looper,
    private val handlerThread: HandlerThread,
    private val fuzzingOrchestrator: FuzzingOrchestrator
) : Handler(looper) {

    companion object {
        private const val TAG = "ServiceHandler"
    }

    override fun handleMessage(msg: Message) {
        val serverPort = msg.arg1
        fuzzingOrchestrator.messageLoop(serverPort)

        Log.v(TAG, "Finished fuzzing")

        // Stop thread and service
        handlerThread.quitSafely()
        service.stopSelf()
    }
}
