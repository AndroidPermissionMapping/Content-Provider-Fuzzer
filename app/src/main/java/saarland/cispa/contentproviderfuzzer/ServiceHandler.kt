package saarland.cispa.contentproviderfuzzer

import android.app.Service
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import saarland.cispa.cp.fuzzing.serialization.ContentProviderApi
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets


class ServiceHandler(
    private val service: Service,
    looper: Looper,
    private val fuzzingDataManager: FuzzingDataManager,
    private val resolverCaller: ResolverCaller,
    private val handlerThread: HandlerThread
) : Handler(looper) {

    companion object {
        private const val TAG = "ServiceHandler"

    }

    override fun handleMessage(msg: Message) {
        Log.v(TAG, "Parsing magic values")
        val fuzzingData: List<ContentProviderApi> = fuzzingDataManager.loadFuzzingData()

        val results: MutableList<FuzzingResult> = mutableListOf()

        Log.v(TAG, "Starting fuzzing")
        for (data in fuzzingData) {

            var r: FuzzingResult? = null
            try {
                resolverCaller.process(data)
                r = FuzzingResult(data, null, null)
            } catch (e: Exception) {
                // Get stacktrace
                val outputStream = ByteArrayOutputStream()
                val printStream = PrintStream(
                    outputStream, true,
                    StandardCharsets.UTF_8.name()
                )
                e.printStackTrace(printStream)

                r = FuzzingResult(data, e.toString(), outputStream.toString())
            } finally {
                if (r != null) {
                    results.add(r)
                }
            }
        }

        fuzzingDataManager.saveResults(results)
        Log.v(TAG, "Finished fuzzing")

        // Stop thread and service
        handlerThread.quitSafely()
        service.stopSelf()
    }
}
