package saarland.cispa.contentproviderfuzzer

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import saarland.cispa.cp.fuzzing.serialization.FuzzingResult
import saarland.cispa.cp.fuzzing.serialization.ResolverCallUri


class ServiceHandler(
    looper: Looper,
    private val inputAndResultsIO: InputAndResultsIO,
    private val resolverCaller: ResolverCaller
) : Handler(looper) {

    companion object {
        private const val TAG = "ServiceHandler"

    }

    override fun handleMessage(msg: Message) {
        Log.v(TAG, "Parsing magic values")
        val fuzzingData: List<ResolverCallUri> = inputAndResultsIO.loadFuzzingData()

        val results: MutableList<FuzzingResult> = mutableListOf()

        Log.v(TAG, "Starting fuzzing")
        for (data in fuzzingData) {

            var r: FuzzingResult? = null
            try {
                resolverCaller.call(data)
                r = FuzzingResult(data, null)
            } catch (e: Exception) {
                r = FuzzingResult(data, e.toString())
            } finally {
                if (r != null) {
                    results.add(r)
                }
            }
        }

        inputAndResultsIO.saveResults(results)
        Log.v(TAG, "Finished fuzzing")
    }
}
