package saarland.cispa.contentproviderfuzzer

import android.util.Log
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

class ConnectionToDynamo {

    companion object {
        private const val TAG = "ConnectionToDynamo"
        private const val MESSAGE_KILL = "Kill"
    }

    private lateinit var socket: ZMQ.Socket

    fun connect(port: Int) {
        if (!::socket.isInitialized) {
            Log.v(TAG, "Connecting to Dynamo")

            //  Socket to talk to server
            val context = ZContext()
            socket = context.createSocket(SocketType.REQ)
            socket.connect("tcp://localhost:$port")
        }
    }

    fun sendReady() {
        sendMessage("Ready")
    }

    fun receiveMessage(): String {
        val messageBytes = socket.recv(0)
        val message = String(messageBytes, ZMQ.CHARSET)
        Log.v(TAG, "Received $message")
        return message
    }

    fun sendMessage(message: String) {
        socket.send(message.toByteArray(ZMQ.CHARSET), 0)
    }

    fun isKillSignal(message: String): Boolean {
        return MESSAGE_KILL == message
    }
}