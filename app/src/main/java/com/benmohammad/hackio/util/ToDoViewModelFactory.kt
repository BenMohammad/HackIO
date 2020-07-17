package com.benmohammad.hackio.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.benmohammad.hackio.Injection.Injection
import com.benmohammad.hackio.taskdetail.TaskDetailActionProcessorHolder
import com.benmohammad.hackio.taskdetail.TaskDetailViewModel
import com.benmohammad.hackio.tasks.TasksActionProcessorHolder
import com.benmohammad.hackio.tasks.TasksViewModel
import java.lang.IllegalArgumentException

class ToDoViewModelFactory private constructor(
    private val applicationContext: Context
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass == TasksViewModel::class.java) {
            return TasksViewModel(
                TasksActionProcessorHolder(
                    Injection.provideTaskRepository(applicationContext),
                    Injection.provideSchedulerProvider())) as T
        } else if(modelClass == TaskDetailViewModel::class.java) {
           return TaskDetailViewModel(
               TaskDetailActionProcessorHolder(
                   Injection.provideTaskRepository(applicationContext),
                   Injection.provideSchedulerProvider())) as T
        }
        throw IllegalArgumentException("unknown model class "+ modelClass)
    }

    companion object : SingletonHolderSingleArg<ToDoViewModelFactory, Context>(
        ::ToDoViewModelFactory
    )
}