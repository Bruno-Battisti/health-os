package com.healthos.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.healthos.app.domain.usecase.SyncNowUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncNowUseCase: SyncNowUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result =
        if (syncNowUseCase().isSuccess) Result.success() else Result.retry()
}
