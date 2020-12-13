package saarland.cispa.contentproviderfuzzer

import android.util.Log
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import saarland.cispa.cp.fuzzing.serialization.ContentProviderApi
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import saarland.cispa.cp.fuzzing.serialization.FuzzingResultSerializer


class WorkerConnection(private val cpFuzzer: ContentProviderFuzzer) {

    companion object {
        private const val TAG = "WorkerConnection"

        private const val MESSAGE_KILL = "Kill"
        private const val MESSAGE_ACK = "Ack"
    }

    private lateinit var socket: ZMQ.Socket

    @Synchronized
    fun messageLoop(port: Int) {
        val context = ZContext()
        connectToDynamo(context, port)

        while (true) {
            sendReadyToDynamo()
            val message = receiveMessage()

            if (isKillSignal(message)) break
            fuzzContentProvider(message)
        }
    }

    private fun connectToDynamo(context: ZContext, port: Int) {
        if (!::socket.isInitialized) {
            Log.v(TAG, "Connecting to Dynamo")

            //  Socket to talk to server
            socket = context.createSocket(SocketType.REQ)
            socket.connect("tcp://localhost:$port")
        }
    }

    private fun sendReadyToDynamo() {
        Log.v(TAG, "Sending Ready")
        val request = "Ready"
        socket.send(request.toByteArray(ZMQ.CHARSET), 0)
    }

    private fun receiveMessage(): String {
        val messageBytes = socket.recv(0)
        val message = String(messageBytes, ZMQ.CHARSET)
        Log.v(TAG, "Received $message")
        return message
    }

    private fun isKillSignal(message: String): Boolean {
        return MESSAGE_KILL == message
    }

    private fun fuzzContentProvider(fuzzingInput: String) {
        val fuzzingData: List<ContentProviderApi> = cpFuzzer.loadFuzzingData(fuzzingInput)

        Log.v(TAG, "Starting fuzzing")
        for (api in fuzzingData) {
            val result = cpFuzzer.fuzzApi(api)
            sendApiResult(result)
        }
    }

    private fun sendApiResult(result: FuzzingResult) {
        val json = FuzzingResultSerializer.toJson(result)
        sendMessage(json)
        waitForAck()
    }

    private fun waitForAck(): Boolean {
        val feedback = receiveMessage()
        val isAck = feedback == MESSAGE_ACK
        if (!isAck) {
            Log.e(TAG, "Ack expected, but got $feedback")
        }
        return isAck
    }


    private fun sendMessage(message: String) {
        socket.send(message.toByteArray(ZMQ.CHARSET), 0)
    }
}