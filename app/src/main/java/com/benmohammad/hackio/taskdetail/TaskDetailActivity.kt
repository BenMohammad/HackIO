package com.benmohammad.hackio.taskdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benmohammad.hackio.R

class TaskDetailActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taskdetail_act)
    }

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }
}