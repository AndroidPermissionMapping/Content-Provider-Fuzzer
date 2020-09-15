package saarland.cispa.contentproviderfuzzer

import android.content.Context
import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class FuzzingIOData(val api_type: String, val uri: String, val method: String)

class InputAndResultsIO(private val context: Context) {

    companion object {
        private const val MAGIC_VALUES_FILE_NAME = "magic_values.json"
        private const val RESULTS_FILE_NAME = "results.json"
    }

    fun loadFuzzingData(): List<FuzzingData> {
        val appDataDir = context.filesDir
        val inputFile = File(appDataDir, MAGIC_VALUES_FILE_NAME)
        val jsonString = inputFile.readText()

        val jsonData = Json.decodeFromString(ListSerializer(FuzzingIOData.serializer()), jsonString)
        return parseMagicValuesForCall(jsonData)
    }

    fun saveResults(failedPermissionChecks: List<FuzzingData>) {
        val results: MutableList<FuzzingIOData> = mutableListOf()

        for (data in failedPermissionChecks) {
            when (data) {
                is ResolverCallUri -> {
                    val r = FuzzingIOData("call", data.uri.toString(), data.method)
                    results.add(r)
                }
            }
        }

        val outputJsonString: String = Json
            .encodeToString(ListSerializer(FuzzingIOData.serializer()), results)

        val appDataDir = context.filesDir
        val inputFile = File(appDataDir, RESULTS_FILE_NAME)
        inputFile.writeText(outputJsonString)
    }


    private fun parseMagicValuesForCall(jsonData: List<FuzzingIOData>): List<FuzzingData> {
        val resultList = mutableListOf<FuzzingData>()
        for (inputJsonData in jsonData) {
            val uri = Uri.parse(inputJsonData.uri)
            val data = ResolverCallUri(uri, inputJsonData.method, null, null)
            resultList.add(data)
        }
        return resultList
    }
}