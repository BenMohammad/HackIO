package com.benmohammad.hackio.tasks

import android.app.TaskInfo
import com.benmohammad.hackio.data.Task
import com.benmohammad.hackio.mvibase.MviIntent

sealed class TasksIntent: MviIntent {
    object InitialIntent : TasksIntent()

    data class RefreshIntent(val forceUpdate: Boolean): TasksIntent()
    data class ActivateTaskIntent(val task: Task): TasksIntent()
    data class CompleteTaskIntent(val task: Task): TasksIntent()
    object ClearCompletedTaskIntent: TasksIntent()
    data class ChangeFilterIntent(val filterTYpe: TasksFilterType): TasksIntent()
}