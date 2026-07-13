package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.DownloadStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: DownloadStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): DownloadStatus {
        return try {
            DownloadStatus.valueOf(value)
        } catch (e: Exception) {
            DownloadStatus.PENDING
        }
    }
}
