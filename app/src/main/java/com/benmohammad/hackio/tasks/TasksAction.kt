package com.benmohammad.hackio.tasks

import com.benmohammad.hackio.data.Task
import com.benmohammad.hackio.mvibase.MviAction

sealed class TasksAction: MviAction {

    data class LoadTasksAction(
        val forceUpdate: Boolean ,
        val filterType: TasksFilterType?
    ): TasksAction()

    data class ActivateTaskAction(val task: Task): TasksAction()
    data class CompleteTaskAction(val task: Task): TasksAction()
    object ClearCompletedTasksAction: TasksAction()
}