package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download_items ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM download_items WHERE id = :id LIMIT 1")
    suspend fun getDownloadById(id: String): DownloadItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(item: DownloadItem)

    @Update
    suspend fun updateDownload(item: DownloadItem)

    @Query("UPDATE download_items SET status = :status, progress = :progress, downloadedBytes = :downloadedBytes, totalBytes = :totalBytes, speed = :speed WHERE id = :id")
    suspend fun updateProgress(id: String, status: DownloadStatus, progress: Float, downloadedBytes: Long, totalBytes: Long, speed: String)

    @Query("UPDATE download_items SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: DownloadStatus)

    @Delete
    suspend fun deleteDownload(item: DownloadItem)

    @Query("DELETE FROM download_items WHERE id = :id")
    suspend fun deleteDownloadById(id: String)

    @Query("SELECT * FROM download_items WHERE status IN ('PENDING', 'DOWNLOADING', 'CONVERTING')")
    suspend fun getActiveDownloads(): List<DownloadItem>
}
