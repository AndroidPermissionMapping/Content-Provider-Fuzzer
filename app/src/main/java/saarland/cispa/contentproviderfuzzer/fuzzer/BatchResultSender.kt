package saarland.cispa.contentproviderfuzzer.fuzzer

import saarland.cispa.contentproviderfuzzer.ConnectionToDynamo
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import saarland.cispa.cp.fuzzing.serialization.FuzzingResultSerializer

class BatchResultSender(private val connectionToDynamo: ConnectionToDynamo) {

    companion object {
        private const val MSG_DONE_SENDING_RESULTS = "Done sending"
        private const val MSG_ASK_FOR_RESULTS = "Send results"

        private const val BUCKET_SIZE = 1000
    }

    fun send(resultList: List<FuzzingResult>) {
        waitUntilServerAsksForResults()
        sendAllResults(resultList)
        tellServerThatAllResultsAreTransmitted()
    }

    private fun waitUntilServerAsksForResults() {
        val message = connectionToDynamo.receiveMessage()
        if (message != MSG_ASK_FOR_RESULTS) {
            throw IllegalStateException("Server didn't ask for results.")
        }
    }

    private fun sendAllResults(resultList: List<FuzzingResult>) {
        val partitionedResults = partitionResults(resultList)
        for (result in partitionedResults) {
            sendResult(result)
        }
    }

    private fun tellServerThatAllResultsAreTransmitted() {
        connectionToDynamo.sendMessage(MSG_DONE_SENDING_RESULTS)
    }

    private fun partitionResults(sourceList: List<FuzzingResult>)
            : List<List<FuzzingResult>> {
        val resultList = mutableListOf<List<FuzzingResult>>()

        var startIndex = 0

        val srcListSize = sourceList.size
        var endIndex =
            if (srcListSize > BUCKET_SIZE) BUCKET_SIZE else srcListSize

        while (!sentAllResults(endIndex, srcListSize)) {
            val subList = sourceList.subList(startIndex, endIndex)
            resultList.add(subList)

            startIndex = endIndex
            endIndex = nextEndIndex(srcListSize, endIndex)
        }

        return resultList
    }

    private fun sendResult(result: List<FuzzingResult>) {
        val json = FuzzingResultSerializer.toJson(result)
        connectionToDynamo.sendMessage(json)

        val m = connectionToDynamo.receiveMessage()
        if (m != Fuzzer.MESSAGE_ACK) {
            throw IllegalStateException("Expected Ack but got $m")
        }
    }

    private fun sentAllResults(endIndex: Int, srcListSize: Int): Boolean {
        return endIndex > srcListSize
    }

    private fun nextEndIndex(srcListSize: Int, lastEndIndex: Int): Int {
        return if (srcListSize > BUCKET_SIZE) {
            lastEndIndex + BUCKET_SIZE
        } else {
            srcListSize + 1
        }
    }
}