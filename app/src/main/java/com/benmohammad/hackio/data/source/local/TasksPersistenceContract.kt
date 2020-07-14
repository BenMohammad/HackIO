package com.benmohammad.hackio.data.source.local

import android.provider.BaseColumns

object TasksPersistenceContract {

    object TaskEntry: BaseColumns {
        const val TABLE_NAME = "tasks"
        const val NAME_ENTRY_ID = "entryId"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_COMPLETED = "completed"
    }
}