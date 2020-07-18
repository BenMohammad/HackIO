package com.benmohammad.hackio.addedittask

import androidx.lifecycle.ViewModel
import com.benmohammad.hackio.addedittask.AddEditTaskAction.CreateTaskAction
import com.benmohammad.hackio.addedittask.AddEditTaskAction.UpdateTaskAction
import com.benmohammad.hackio.addedittask.AddEditTaskResult.*
import com.benmohammad.hackio.mvibase.MviViewModel
import com.benmohammad.hackio.taskdetail.TaskDetailResult
import com.benmohammad.hackio.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class AddEditTaskViewModel(
    private val actionProcessorHolder: AddEditTaskActionProcessorHolder
): ViewModel(), MviViewModel<AddEditTaskIntent, AddEditTaskViewState> {

    private val intentsSubject: PublishSubject<AddEditTaskIntent> = PublishSubject.create()
    private val statesObservable: Observable<AddEditTaskViewState> = compose()
    private val disposables = CompositeDisposable()

    private val intentFilter: ObservableTransformer<AddEditTaskIntent, AddEditTaskIntent>
    get() = ObservableTransformer { intents ->
        intents.publish { shared ->
            Observable.merge<AddEditTaskIntent>(
                shared.ofType(AddEditTaskIntent.InitialIntent::class.java).take(1),
                shared.notOfType(AddEditTaskIntent.InitialIntent::class.java)
            )
        }
    }



    override fun processIntents(intents: Observable<AddEditTaskIntent>) {
        disposables.add(intents.subscribe(intentsSubject::onNext))
    }

    override fun states(): Observable<AddEditTaskViewState> = statesObservable

    private fun compose(): Observable<AddEditTaskViewState> {
        return intentsSubject
            .compose<AddEditTaskIntent>(intentFilter)
            .map<AddEditTaskAction>(this::actionFromIntent)
            .filter { action -> action !is AddEditTaskAction.SkipMe }
            .compose(actionProcessorHolder.actionProcessor)
            .scan(AddEditTaskViewState.idle(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: AddEditTaskIntent): AddEditTaskAction {
        return when (intent) {
            is AddEditTaskIntent.InitialIntent -> {
                if(intent.taskId == null) {
                    AddEditTaskAction.SkipMe
                } else {
                    AddEditTaskAction.PopulateTaskAction(taskId = intent.taskId)
                }
            }
            is AddEditTaskIntent.SaveTask -> {
                val (taskId, title, description) = intent
                if(taskId == null) {
                    CreateTaskAction(title, description)
                } else {
                    UpdateTaskAction(taskId, title, description)
                }
            }
        }
    }

    override fun onCleared() {
        disposables.dispose()
    }

    companion object {
        private val reducer = BiFunction{previousState: AddEditTaskViewState, result: AddEditTaskResult ->
            when(result) {
                is PopulateTaskResult -> when (result) {
                    is PopulateTaskResult.Success -> {
                        result.task.let {task ->
                            if(task.active) {
                                previousState.copy(title = task.title!!, description = task.description!!)
                            } else {
                                previousState
                            }
                        }
                    }
                    is PopulateTaskResult.Failure -> previousState.copy(error = result.error)
                    is PopulateTaskResult.InFlight -> previousState
                }
                is CreateTaskResult -> when (result) {
                    is CreateTaskResult.Success -> previousState.copy(isEmpty = false, isSaved = true)
                    is CreateTaskResult.Empty -> previousState.copy(isEmpty = true)
                }
                is UpdateTaskResult -> previousState.copy(isSaved = true)

            }        }
    }
}