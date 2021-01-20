package saarland.cispa.contentproviderfuzzer.fuzzer

import saarland.cispa.contentproviderfuzzer.ConnectionToDynamo
import saarland.cispa.contentproviderfuzzer.ResolverCaller
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult


class BatchFuzzer(
    private val connectionToDynamo: ConnectionToDynamo,
    resolverCaller: ResolverCaller
) : Fuzzer(resolverCaller) {

    companion object {
        private const val MSG_DONE_FUZZING = "Done fuzzing"
    }

    private val resultSender = BatchResultSender(connectionToDynamo)

    override fun fuzzApis(job: FuzzingJob) {
        val results = fuzzContentProvider(job)
        resultSender.send(results)
    }

    private fun fuzzContentProvider(job: FuzzingJob): List<FuzzingResult> {
        val results = job.fuzzingRequests.map { api -> fuzzApi(api) }
        connectionToDynamo.sendMessage(MSG_DONE_FUZZING)
        return results
    }
}