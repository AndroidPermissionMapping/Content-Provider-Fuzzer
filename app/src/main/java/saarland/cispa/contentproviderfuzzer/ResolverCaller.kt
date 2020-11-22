package saarland.cispa.contentproviderfuzzer

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import saarland.cispa.cp.fuzzing.serialization.*

class ResolverCaller(private val resolver: ContentResolver) {

    fun process(data: ContentProviderApi) {
        val uri = Uri.parse(data.uri)
        when (data) {
            is ResolverCallUri -> resolver.call(uri, data.method, data.arg, null)

            is ResolverQueryApi1 -> {
                resolver.query(
                    uri,
                    data.projection,
                    data.selection,
                    data.selectionArgs,
                    data.sortOrder
                )?.close()
            }

            is ResolverCallInsert -> {
                val contentValues = ContentValues().apply {
                    putNull(data.contentValue.key)
                }

                resolver.insert(uri, contentValues)
            }


            is ResolverCallUpdate -> {
                val contentValues = ContentValues().apply {
                    putNull(data.contentValue.key)
                }

                resolver.update(uri, contentValues, data.selection, null)
            }

            is ResolverCallDelete -> {
                resolver.delete(uri, data.selection, null)
            }
        }
    }
}
