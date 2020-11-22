package saarland.cispa.contentproviderfuzzer

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import saarland.cispa.cp.fuzzing.serialization.*

class ResolverCaller(private val resolver: ContentResolver) {

    fun process(data: ContentProviderApi) {
        val uri = Uri.parse(data.uri)
        when (data) {
            is ResolverCallUri -> {
                val bundle = parseBundleKey(data.extras)
                when (data.apiLevel) {
                    CallApiLevel.API_11 -> resolver.call(uri, data.method, data.arg, bundle)
                    CallApiLevel.API_29 -> resolver.call(data.uri, data.method, data.arg, bundle)
                }

            }

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

    private fun parseBundleKey(extras: BundleKey?): Bundle? {
        if (extras == null) {
            return null
        }

        val bundle = Bundle()
        when (extras.type) {
            JavaType.STRING -> bundle.putString(extras.key, null)
            JavaType.INT -> bundle.putInt(extras.key, 0)
            JavaType.LONG -> bundle.putLong(extras.key, 0)
            JavaType.BOOL -> bundle.putBoolean(extras.key, true)
            JavaType.OBJECT -> bundle.putSerializable(extras.key, null)
            JavaType.BYTES -> bundle.putByteArray(extras.key, null)
        }
        return bundle
    }
}
