package com.example.data.repository

import com.example.data.database.DownloadDao
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

class DownloadRepository(private val downloadDao: DownloadDao) {
    val allDownloads: Flow<List<DownloadItem>> = downloadDao.getAllDownloads()

    suspend fun getDownloadById(id: String): DownloadItem? = downloadDao.getDownloadById(id)

    suspend fun insert(item: DownloadItem) = downloadDao.insertDownload(item)

    suspend fun update(item: DownloadItem) = downloadDao.updateDownload(item)

    suspend fun updateProgress(
        id: String,
        status: DownloadStatus,
        progress: Float,
        downloadedBytes: Long,
        totalBytes: Long,
        speed: String
    ) {
        downloadDao.updateProgress(id, status, progress, downloadedBytes, totalBytes, speed)
    }

    suspend fun updateStatus(id: String, status: DownloadStatus) {
        downloadDao.updateStatus(id, status)
    }

    suspend fun delete(item: DownloadItem) = downloadDao.deleteDownload(item)

    suspend fun deleteById(id: String) = downloadDao.deleteDownloadById(id)

    suspend fun getActiveDownloads(): List<DownloadItem> = downloadDao.getActiveDownloads()
}
