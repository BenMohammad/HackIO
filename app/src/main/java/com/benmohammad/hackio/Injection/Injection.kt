package com.benmohammad.hackio.Injection

import android.content.Context
import com.benmohammad.hackio.data.source.TasksRepository
import com.benmohammad.hackio.data.source.local.TasksLocalDataSource
import com.benmohammad.hackio.data.source.remote.TasksRemoteDataSource
import com.benmohammad.hackio.util.schedulers.BaseSchedulerProvider
import com.benmohammad.hackio.util.schedulers.SchedulerProvider

object Injection {

    fun provideTaskRepository(context: Context): TasksRepository {
        return TasksRepository.getInstance(
            TasksRemoteDataSource,
            TasksLocalDataSource.getInstance(context, provideSchedulerProvider())
        )
    }

    fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider

}