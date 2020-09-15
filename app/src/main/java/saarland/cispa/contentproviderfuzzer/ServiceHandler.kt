package saarland.cispa.contentproviderfuzzer

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json


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
        val fuzzingData: List<FuzzingData> = inputAndResultsIO.loadFuzzingData()
        val failedPermissionChecks: MutableList<FuzzingData> = mutableListOf()

        Log.v(TAG, "Starting fuzzing")
        for (data in fuzzingData) {

            try {
                resolverCaller.call(data)
            } catch (e: SecurityException) {
                failedPermissionChecks.add(data)
            } catch (e: NullPointerException) {
                Log.v(TAG, "NullPointerException for: $data")
            } catch (e: UnsupportedOperationException) {
                // No need to take care of it
            }
        }

        inputAndResultsIO.saveResults(failedPermissionChecks)
        Log.v(TAG, "Finished fuzzing")
    }
}