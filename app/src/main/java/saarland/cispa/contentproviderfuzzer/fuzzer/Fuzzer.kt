package saarland.cispa.contentproviderfuzzer.fuzzer

import kotlinx.serialization.Serializable
import saarland.cispa.contentproviderfuzzer.ResolverCaller
import saarland.cispa.cp.fuzzing.serialization.ContentProviderApi
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets

@Serializable
data class FuzzingJob(val batchRequests: Boolean, val fuzzingRequests: List<ContentProviderApi>)

abstract class Fuzzer(private val resolverCaller: ResolverCaller) {

    companion object {
        const val MESSAGE_ACK = "Ack"
    }

    abstract fun fuzzApis(job: FuzzingJob)

    protected fun fuzzApi(api: ContentProviderApi): FuzzingResult {
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