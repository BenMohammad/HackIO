package com.benmohammad.hackio.taskdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benmohammad.hackio.R
import com.benmohammad.hackio.addedittask.AddEditTaskFragment
import com.benmohammad.hackio.util.addFragmentToActivity

class TaskDetailActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taskdetail_act)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)

        }

        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        if(supportFragmentManager.findFragmentById(R.id.contentFrame) == null) {
            addFragmentToActivity(supportFragmentManager, TaskDetailFragment(taskId), R.id.contentFrame)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }
}