package saarland.cispa.contentproviderfuzzer

import android.content.ContentResolver
import android.net.Uri
import android.util.Log

import saarland.cispa.cp.fuzzing.serialization.ResolverCallUri

class ResolverCaller(private val resolver: ContentResolver) {

    companion object {
        private const val TAG = "ResolverCaller"
    }

    fun call(data: ResolverCallUri) {
        /* when (data) {
            is ResolverCallUri -> {*/
                try {
                    val uri = Uri.parse(data.uri)
                    resolver.call(uri, data.method, data.arg, null)
                } catch (e: IllegalArgumentException) {
                    Log.v(TAG, "Unknown uri: ${data.uri}")
                }
            /* }

            is ResolverCallAuthority -> {
                resolver.call(data.authority, data.method, data.arg, data.extras)
            }

            is ResolverQuery -> {
                val cursor = resolver.query(data.uri, data.projection, data.queryArgs, null)
                cursor?.close()
            }

            is ResolverInsert -> {
                resolver.insert(data.uri, data.contentValues)
            }

            is ResolverUpdate -> {
                resolver.update(data.uri, data.contentValues, data.where, data.selectionArgs)
            }

            is ResolverDelete -> {
                resolver.delete(data.uri, data.where, data.selectionArgs)
            }
        } */
    }
}
