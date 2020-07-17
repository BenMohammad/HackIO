package com.benmohammad.hackio.tasks

import androidx.lifecycle.ViewModel
import com.benmohammad.hackio.data.Task
import com.benmohammad.hackio.mvibase.MviViewModel
import com.benmohammad.hackio.tasks.TasksResult.LoadTaskResult
import com.benmohammad.hackio.tasks.TasksViewState.UiNotification.*
import com.benmohammad.hackio.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class TasksViewModel(
    private val actionProcessorHolder: TasksActionProcessorHolder)
    : ViewModel(), MviViewModel<TasksIntent, TasksViewState> {

    private val intentSubject: PublishSubject<TasksIntent> = PublishSubject.create()
    private val statesObservable: Observable<TasksViewState> = compose()
    private val disposables = CompositeDisposable()

    private val intentFilter: ObservableTransformer<TasksIntent, TasksIntent>
    get() = ObservableTransformer { intents ->
        intents.publish { shared ->
            Observable.merge(
            shared.ofType(TasksIntent.InitialIntent::class.java).take(1),
            shared.notOfType(TasksIntent.InitialIntent::class.java)
            )
        }
    }

    override fun processIntents(intents: Observable<TasksIntent>) {
        disposables.add(intents.subscribe(intentSubject::onNext))
    }

    override fun states(): Observable<TasksViewState> = statesObservable

    private fun compose(): Observable<TasksViewState> {
        return intentSubject
            .compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(TasksViewState.idle(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: TasksIntent): TasksAction {
        return when (intent) {
            is TasksIntent.InitialIntent -> TasksAction.LoadTasksAction(true, TasksFilterType.ALL_TASKS)
            is TasksIntent.RefreshIntent -> TasksAction.LoadTasksAction(intent.forceUpdate,null)
            is TasksIntent.ActivateTaskIntent -> TasksAction.ActivateTaskAction(intent.task)
            is TasksIntent.CompleteTaskIntent -> TasksAction.CompleteTaskAction(intent.task)
            is TasksIntent.ClearCompletedTaskIntent -> TasksAction.ClearCompletedTasksAction
            is TasksIntent.ChangeFilterIntent -> TasksAction.LoadTasksAction(false, intent.filterTYpe)
        }
    }

    override fun onCleared() {
        disposables.dispose()
    }

    companion object {
        private val reducer = BiFunction{previousState: TasksViewState, result: TasksResult ->
            when(result) {
                is LoadTaskResult -> when (result) {
                    is LoadTaskResult.Success -> {
                        val filterType = result.filterType ?: previousState.tasksFilterType
                        val tasks = filterTasks(result.tasks, filterType)
                        previousState.copy(
                            isLoading = false,
                            tasks = tasks,
                            tasksFilterType = filterType
                        )
                    }
                    is LoadTaskResult.Failure -> previousState.copy(isLoading = false, error = result.error)
                    is LoadTaskResult.InFlight -> previousState.copy(isLoading = true)
                }
                is TasksResult.CompleteTaskResult -> when (result) {
                    is TasksResult.CompleteTaskResult.Success ->
                        previousState.copy(
                            uiNotification = TASK_COMPLETE,
                            tasks = filterTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is TasksResult.CompleteTaskResult.Failure -> previousState.copy(error = result.error)
                    is TasksResult.CompleteTaskResult.InFlight -> previousState
                    is TasksResult.CompleteTaskResult.HideUiNotification ->
                        if(previousState.uiNotification == TASK_COMPLETE) {
                            previousState.copy(uiNotification = null)
                        } else {
                            previousState
                        }
                }
                is TasksResult.ActivateTaskResult -> when (result) {
                    is TasksResult.ActivateTaskResult.Success ->
                        previousState.copy(
                            uiNotification = TASK_ACTIVATED,
                            tasks = filterTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is TasksResult.ActivateTaskResult.Failure -> previousState.copy(error = result.error)
                    is TasksResult.ActivateTaskResult.InFlight -> previousState
                    is TasksResult.ActivateTaskResult.HideUiNotification ->
                        if(previousState.uiNotification == TASK_ACTIVATED) {
                            previousState.copy(uiNotification = null)
                        } else {
                            previousState
                        }
                }
                is TasksResult.ClearCompletedTasksResult -> when (result) {
                    is TasksResult.ClearCompletedTasksResult.Success ->
                        previousState.copy(
                            uiNotification = COMPLETE_TASKS_CLEARED,
                            tasks = filterTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is TasksResult.ClearCompletedTasksResult.Failure -> previousState.copy(error = result.error)
                    is TasksResult.ClearCompletedTasksResult.InFlight -> previousState
                    is TasksResult.ClearCompletedTasksResult.HideUiNotification ->
                        if(previousState.uiNotification == COMPLETE_TASKS_CLEARED) {
                            previousState.copy(
                                uiNotification = null
                            )
                        } else {
                            previousState
                        }
                }
            }
        }


    private fun filterTasks(
        tasks: List<Task>,
        filterType: TasksFilterType
        ): List<Task> {
        return when (filterType) {
            TasksFilterType.ALL_TASKS -> tasks
            TasksFilterType.ACTIVE_TASKS -> tasks.filter(Task::active)
            TasksFilterType.COMPLETED_TASKS -> tasks.filter(Task::completed)
        }
    }

}}
