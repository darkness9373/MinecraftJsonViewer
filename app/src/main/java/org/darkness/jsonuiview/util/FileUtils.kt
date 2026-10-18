package org.darkness.jsonuiview.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FileUtils {
    suspend fun readUriAsString(uri: Uri, contentResolver: ContentResolver): String {
        return withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes().decodeToString()
            } ?: throw IllegalStateException("Could not read file: $uri")
        }
    }

    fun getDisplayName(uri: Uri, contentResolver: ContentResolver): String {
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            return try {
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) cursor.getString(nameIndex) else uri.lastPathSegment ?: uri.toString()
                } else {
                    uri.lastPathSegment ?: uri.toString()
                }
            } finally {
                cursor?.close()
            }
        } else {
            uri.lastPathSegment ?: uri.toString()
        }
    }

    fun getFileExtension(uri: Uri): String {
        val path = uri.path ?: return ""
        val lastDot = path.lastIndexOf('.')
        return if (lastDot >= 0) path.substring(lastDot + 1).lowercase() else ""
    }

    fun isJsonFile(uri: Uri): Boolean {
        return getFileExtension(uri) == "json"
    }
}