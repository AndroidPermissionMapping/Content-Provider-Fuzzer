package saarland.cispa.contentproviderfuzzer

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle

sealed class FuzzingData

data class ResolverCallUri(
    val uri: Uri,
    val method: String,
    val arg: String?,
    val extras: Bundle?
) : FuzzingData()

data class ResolverCallAuthority(
    val authority: String,
    val method: String,
    val arg: String?,
    val extras: Bundle?
) : FuzzingData()

data class ResolverQuery(
    val uri: Uri,
    val projection: Array<String>,
    val queryArgs: Bundle
) : FuzzingData() {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

data class ResolverInsert(
    val uri: Uri,
    val contentValues: ContentValues?
) : FuzzingData()

data class ResolverUpdate(
    val uri: Uri,
    val contentValues: ContentValues?,
    val where: String?,
    val selectionArgs: Array<String>?
) : FuzzingData() {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}


data class ResolverDelete(
    val uri: Uri,
    val where: String?,
    val selectionArgs: Array<String>?
) : FuzzingData() {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}