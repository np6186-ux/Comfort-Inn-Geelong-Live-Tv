package com.comfortinn.geelong.tv

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.comfortinn.geelong.tv.data.worker.SyncChannelsWorker
import java.util.concurrent.TimeUnit

class ComfortInnApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val syncRequest = PeriodicWorkRequestBuilder<SyncChannelsWorker>(12, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "comfort_inn_channel_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        val initialSync = OneTimeWorkRequestBuilder<SyncChannelsWorker>().build()
        WorkManager.getInstance(this).enqueue(initialSync)
    }
}
