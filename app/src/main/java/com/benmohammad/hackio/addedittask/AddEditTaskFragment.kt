package com.benmohammad.hackio.addedittask

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.benmohammad.hackio.R
import com.benmohammad.hackio.mvibase.MviView
import com.benmohammad.hackio.util.ToDoViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlin.LazyThreadSafetyMode.NONE

class AddEditTaskFragment: Fragment(), MviView<AddEditTaskIntent, AddEditTaskViewState> {

    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var fab: FloatingActionButton

    private val disposables = CompositeDisposable()
    private val viewModel: AddEditTaskViewModel by lazy(NONE) {
        ViewModelProvider(this, ToDoViewModelFactory.getInstance(requireContext())).get(AddEditTaskViewModel::class.java)
    }

    private val argumentTaskId: String?
    get() = arguments?.getString(ARGUMENT_EDIT_TASK_ID)

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.addtask_frag, container, false)
            .also{
                title = it.findViewById(R.id.add_task_title)
                description = it.findViewById(R.id.add_task_description)
                setHasOptionsMenu(true)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab = requireActivity()!!.findViewById(R.id.fab_edit_task_done)
        fab.setImageResource(R.drawable.ic_done)

        bind()
    }

    private fun bind() {
        disposables.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())
    }


    override fun intents(): Observable<AddEditTaskIntent> {
        return Observable.merge(initialIntent(), saveTaskIntent())
    }

    private fun initialIntent(): Observable<AddEditTaskIntent.InitialIntent> {
        return Observable.just(AddEditTaskIntent.InitialIntent(argumentTaskId))
    }

    private fun saveTaskIntent(): Observable<AddEditTaskIntent.SaveTask> {
        return RxView.clicks(fab).map{
            AddEditTaskIntent.SaveTask(argumentTaskId, title.text.toString(), description.text.toString())
        }
    }



    override fun render(state: AddEditTaskViewState) {
        if(state.isSaved) {
            showTasksList()
            return
        }

        if(state.isEmpty) {
            showEmptyTaskError()
        }

        if(state.title.isNotEmpty()) {
            setTitle(state.title)
        }

        if(state.description.isNotEmpty()) {
            setDescription(state.description)
        }
    }

    private fun showEmptyTaskError() {
        Snackbar.make(title, getString(R.string.empty_task_message), Snackbar.LENGTH_SHORT).show()
    }

    private fun showTasksList() {
        requireActivity()!!.setResult(Activity.RESULT_OK)
        requireActivity()!!.finish()
    }

    private fun setTitle(title: String) {
        this.title.text = title
    }

    private fun setDescription(description: String) {
        this.description.text = description
    }
    companion object{
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"
        operator fun invoke(): AddEditTaskFragment = AddEditTaskFragment()
    }
}