package saarland.cispa.contentproviderfuzzer

import kotlinx.serialization.json.Json
import saarland.cispa.contentproviderfuzzer.fuzzer.BatchFuzzer
import saarland.cispa.contentproviderfuzzer.fuzzer.FuzzingJob
import saarland.cispa.contentproviderfuzzer.fuzzer.SerialFuzzer


class FuzzingOrchestrator(resolverCaller: ResolverCaller) {

    private val connectionToDynamo = ConnectionToDynamo()
    private val serialFuzzer = SerialFuzzer(connectionToDynamo, resolverCaller)
    private val batchFuzzer = BatchFuzzer(connectionToDynamo, resolverCaller)

    @Synchronized
    fun messageLoop(port: Int) {
        connectionToDynamo.connect(port)

        while (true) {
            connectionToDynamo.sendReady()

            val message = connectionToDynamo.receiveMessage()
            if (connectionToDynamo.isKillSignal(message)) break

            val job = parseFuzzJob(message)
            runJob(job)
        }
    }

    private fun parseFuzzJob(message: String): FuzzingJob {
        return Json.decodeFromString(FuzzingJob.serializer(), message)
    }

    private fun runJob(job: FuzzingJob) {
        if (job.batchRequests) {
            batchFuzzer.fuzzApis(job)
        } else {
            serialFuzzer.fuzzApis(job)
        }
    }

}