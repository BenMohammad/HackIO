package com.benmohammad.hackio.addedittask

import androidx.fragment.app.Fragment
import com.benmohammad.hackio.mvibase.MviView

class AddEditTaskFragment: Fragment() {

    companion object{
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"
        operator fun invoke(): AddEditTaskFragment = AddEditTaskFragment()
    }
}