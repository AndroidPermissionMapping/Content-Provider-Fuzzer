package saarland.cispa.contentproviderfuzzer

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import saarland.cispa.cp.fuzzing.serialization.ContentProviderApi
import saarland.cispa.cp.fuzzing.serialization.ResolverCallInsert
import saarland.cispa.cp.fuzzing.serialization.ResolverCallUri
import saarland.cispa.cp.fuzzing.serialization.ResolverQueryApi1

class ResolverCaller(private val resolver: ContentResolver) {

    companion object {
        private const val TAG = "ResolverCaller"
    }

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

            /* is ResolverCallAuthority -> {
                resolver.call(data.authority, data.method, data.arg, data.extras)
            }



            is ResolverInsert -> {
                resolver.insert(data.uri, data.contentValues)
            }

            is ResolverUpdate -> {
                resolver.update(data.uri, data.contentValues, data.where, data.selectionArgs)
            }

            is ResolverDelete -> {
                resolver.delete(data.uri, data.where, data.selectionArgs)
            } */
        }
    }
}
