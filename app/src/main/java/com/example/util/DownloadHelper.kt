package com.example.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream

object DownloadHelper {

    fun createDownloadFile(context: Context, fileName: String, mimeType: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                resolver.insert(collection, contentValues)
            } else {
                // Legacy devices (API < 29)
                val collection = MediaStore.Files.getContentUri("external")
                resolver.insert(collection, contentValues)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun openOutputStream(context: Context, uri: Uri): OutputStream? {
        return try {
            context.contentResolver.openOutputStream(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
