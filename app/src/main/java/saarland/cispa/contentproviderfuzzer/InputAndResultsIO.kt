package saarland.cispa.contentproviderfuzzer

import android.content.Context
import saarland.cispa.cp.fuzzing.serialization.*
import saarland.cispa.cp.fuzzing.serialization.ResolverCallUri
import java.io.File


class InputAndResultsIO(private val context: Context) {

    companion object {
        private const val MAGIC_VALUES_FILE_NAME = "magic_values.json"
        private const val RESULTS_FILE_NAME = "results.json"
    }

    fun loadFuzzingData(): List<ResolverCallUri> {
        val appDataDir = context.filesDir
        val inputFile = File(appDataDir, MAGIC_VALUES_FILE_NAME)

        return FuzzingDataSerializer.deserialize(inputFile)
    }

    fun saveResults(results: List<FuzzingResult>) {
        val appDataDir = context.filesDir
        val inputFile = File(appDataDir, RESULTS_FILE_NAME)
        OutputSerializer.serialize(inputFile, results)
    }
}
