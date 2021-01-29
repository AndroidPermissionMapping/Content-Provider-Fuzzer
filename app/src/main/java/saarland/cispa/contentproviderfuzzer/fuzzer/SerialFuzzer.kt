package saarland.cispa.contentproviderfuzzer.fuzzer

import kotlinx.serialization.json.Json
import saarland.cispa.contentproviderfuzzer.ConnectionToDynamo
import saarland.cispa.contentproviderfuzzer.ResolverCaller
import saarland.cispa.cp.fuzzing.serialization.ContentProviderApi
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import saarland.cispa.cp.fuzzing.serialization.FuzzingResultSerializer


class SerialFuzzer(
    private val connectionToDynamo: ConnectionToDynamo,
    resolverCaller: ResolverCaller
) : Fuzzer(resolverCaller) {

    override fun fuzzApis(job: FuzzingJob) {
        connectionToDynamo.sendMessage("Ack")

        var apiRequest = connectionToDynamo.receiveMessage()
        while (apiRequest != "Ack") {
            val api = Json.decodeFromString(ContentProviderApi.serializer(), apiRequest)
            val result: FuzzingResult = fuzzApi(api)
            sendResult(result)

            apiRequest = connectionToDynamo.receiveMessage()
        }
    }

    private fun sendResult(result: FuzzingResult) {
        val json = FuzzingResultSerializer.toJson(result)
        connectionToDynamo.sendMessage(json)
    }
}