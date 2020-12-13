package saarland.cispa.contentproviderfuzzer

import saarland.cispa.cp.fuzzing.serialization.ContentProviderApi
import saarland.cispa.cp.fuzzing.serialization.FuzzingData
import saarland.cispa.cp.fuzzing.serialization.FuzzingDataSerializer
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets

class ContentProviderFuzzer(private val resolverCaller: ResolverCaller) {

    fun loadFuzzingData(fuzzingInput: String): List<ContentProviderApi> {
        val fuzzingDataList: List<FuzzingData> = FuzzingDataSerializer.deserialize(fuzzingInput)

        val results = mutableListOf<ContentProviderApi>()
        fuzzingDataList.forEach { fuzzingData -> results.addAll(fuzzingData.data) }
        return results
    }

    fun fuzzApi(api: ContentProviderApi): FuzzingResult {
        return try {
            resolverCaller.process(api)
            FuzzingResult(api, null, null)

        } catch (e: Exception) {
            val stacktrace = extractStacktraceFromException(e)
            FuzzingResult(api, e.toString(), stacktrace)
        }
    }

    private fun extractStacktraceFromException(exception: Exception): String {
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(
            outputStream, true,
            StandardCharsets.UTF_8.name()
        )
        exception.printStackTrace(printStream)
        return outputStream.toString()
    }
}