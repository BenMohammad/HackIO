package com.benmohammad.hackio.addedittask

import com.benmohammad.hackio.mvibase.MviIntent

sealed class AddEditTaskIntent: MviIntent {

    data class InitialIntent(val taskId: String?): AddEditTaskIntent()

    data class SaveTask(
        val taskId: String?,
        val title: String,
        val description: String
    ):AddEditTaskIntent()
}