package saarland.cispa.contentproviderfuzzer.fuzzer

import saarland.cispa.contentproviderfuzzer.ConnectionToDynamo
import saarland.cispa.contentproviderfuzzer.ResolverCaller
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import saarland.cispa.cp.fuzzing.serialization.FuzzingResultSerializer


class BatchFuzzer(
    private val connectionToDynamo: ConnectionToDynamo,
    resolverCaller: ResolverCaller
) : Fuzzer(resolverCaller) {

    override fun fuzzApis(job: FuzzingJob) {
        val results = job.fuzzingRequests.map { api -> fuzzApi(api) }
        sendResults(results)
    }

    private fun sendResults(results: List<FuzzingResult>) {
        val json = FuzzingResultSerializer.toJson(results)
        connectionToDynamo.sendMessage(json)
    }
}