package saarland.cispa.contentproviderfuzzer.fuzzer

import android.util.Log
import saarland.cispa.contentproviderfuzzer.ConnectionToDynamo
import saarland.cispa.contentproviderfuzzer.ResolverCaller
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import saarland.cispa.cp.fuzzing.serialization.FuzzingResultSerializer


class SerialFuzzer(
    private val connectionToDynamo: ConnectionToDynamo,
    resolverCaller: ResolverCaller
) : Fuzzer(resolverCaller) {

    companion object {
        private const val TAG = "SerialFuzzer"
    }

    override fun fuzzApis(job: FuzzingJob) {
        for (api in job.fuzzingRequests) {
            val result: FuzzingResult = fuzzApi(api)
            sendResult(result)
        }
    }

    private fun sendResult(result: FuzzingResult) {
        val json = FuzzingResultSerializer.toJson(result)
        connectionToDynamo.sendMessage(json)
        waitForAck()
    }

    private fun waitForAck(): Boolean {
        val feedback = connectionToDynamo.receiveMessage()
        val isAck = feedback == MESSAGE_ACK
        if (!isAck) {
            Log.e(TAG, "Ack expected, but got $feedback")
        }
        return isAck
    }
}