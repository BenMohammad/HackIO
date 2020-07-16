package com.benmohammad.hackio.taskdetail

import com.benmohammad.hackio.data.Task
import com.benmohammad.hackio.mvibase.MviResult

sealed class TaskDetailResult: MviResult {

    sealed class PopulateTaskResult: TaskDetailResult() {
        data class Success(val task: Task): PopulateTaskResult()
        data class Failure(val error: Throwable): PopulateTaskResult()
        object InFlight: PopulateTaskResult()
    }

    sealed class ActivateTaskResult: TaskDetailResult() {
        data class Success(val task: Task): ActivateTaskResult()
        data class Failure(val error: Throwable): ActivateTaskResult()
        object InFlight: ActivateTaskResult()
        object HideUiNotification: ActivateTaskResult()
    }

    sealed class CompleteTaskResult: TaskDetailResult() {
        data class Success(val task: Task): CompleteTaskResult()
        data class Failure(val error: Throwable): CompleteTaskResult()
        object InFlight: CompleteTaskResult()
        object HideUiNotification: CompleteTaskResult()
    }

    sealed class DeleteTaskResult: TaskDetailResult() {
        data class Success(val task: Task): DeleteTaskResult()
        data class Failure(val error: Throwable): DeleteTaskResult()
        object InFlight: DeleteTaskResult()
    }
}