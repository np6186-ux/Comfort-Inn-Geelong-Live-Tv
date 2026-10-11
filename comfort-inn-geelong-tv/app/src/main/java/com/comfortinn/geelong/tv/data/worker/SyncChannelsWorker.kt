package com.comfortinn.geelong.tv.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.comfortinn.geelong.tv.data.repository.ChannelRepository

class SyncChannelsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repo = ChannelRepository(applicationContext)
        val success = repo.refreshChannelsAndEpg()
        return if (success) Result.success() else Result.retry()
    }
}
