package saarland.cispa.contentproviderfuzzer

import android.content.ContentResolver
import android.util.Log


class ResolverCaller(private val resolver: ContentResolver) {

    companion object {
        private const val TAG = "ResolverCaller"
    }

    fun call(data: FuzzingData) {
        when (data) {
            is ResolverCallUri -> {
                try {
                    resolver.call(data.uri, data.method, data.arg, data.extras)
                } catch (e: IllegalArgumentException) {
                    Log.v(TAG, "Unknown uri: ${data.uri}")
                }
            }

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
        }
    }
}