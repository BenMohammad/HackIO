package com.benmohammad.hackio.taskdetail

import com.benmohammad.hackio.mvibase.MviIntent

sealed class TaskDetailIntent: MviIntent {

    data class InitialIntent(val taskId: String): TaskDetailIntent()
    data class DeleteTaskIntent(val taskId: String): TaskDetailIntent()
    data class ActivateTaskIntent(val taskId: String): TaskDetailIntent()
    data class CompleteTask(val taskId: String): TaskDetailIntent()
}