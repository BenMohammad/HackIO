package com.benmohammad.hackio.taskdetail

import com.benmohammad.hackio.data.source.TasksRepository
import com.benmohammad.hackio.taskdetail.TaskDetailAction.*
import com.benmohammad.hackio.taskdetail.TaskDetailResult.*
import com.benmohammad.hackio.util.pairWithDelay
import com.benmohammad.hackio.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.lang.IllegalArgumentException

class TaskDetailActionProcessorHolder(
    private val tasksRepository: TasksRepository,
    private val schedulerProvider: BaseSchedulerProvider
) {

    private val populateTaskProcessor =
        ObservableTransformer<PopulateTaskAction, PopulateTaskResult> {actions ->
            actions.flatMap { action ->
                tasksRepository.getTask(action.taskId)
                    .toObservable()
                    .map(PopulateTaskResult::Success)
                    .cast(PopulateTaskResult::class.java)
                    .onErrorReturn(PopulateTaskResult::Failure)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWith(PopulateTaskResult.InFlight)
            }
        }

    private val completeTaskProcessor =
        ObservableTransformer<CompleteTaskAction, CompleteTaskResult> { actions ->
            actions.flatMap { action ->
                tasksRepository.completeTask(action.taskId)
                    .andThen(tasksRepository.getTask(action.taskId))
                    .toObservable()
                    .flatMap { task ->
                      pairWithDelay(
                          CompleteTaskResult.Success(task),
                          CompleteTaskResult.HideUiNotification
                      )
                    }
                    .onErrorReturn (CompleteTaskResult::Failure)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWith(CompleteTaskResult.InFlight)
            }
        }

    private val activateTaskProcessor =
        ObservableTransformer<ActivateTaskAction, TaskDetailResult.ActivateTaskResult> { actions ->
            actions.flatMap { action ->
                tasksRepository.activateTask(action.taskId)
                    .andThen(tasksRepository.getTask(action.taskId))
                    .toObservable()
                    .flatMap { task ->
                        pairWithDelay(
                            TaskDetailResult.ActivateTaskResult.Success(task),
                            TaskDetailResult.ActivateTaskResult.HideUiNotification
                        )
                    }
                    .onErrorReturn(TaskDetailResult.ActivateTaskResult::Failure)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWith(TaskDetailResult.ActivateTaskResult.InFlight)
            }
        }

    private val deleteTaskProcessor =
        ObservableTransformer<DeleteTaskAction, DeleteTaskResult> { actions ->
            actions.flatMap { action ->
                tasksRepository.deleteTask(action.taskId)
                    .andThen(Observable.just(DeleteTaskResult.Success))
                    .cast(DeleteTaskResult::class.java)
                    .onErrorReturn(DeleteTaskResult::Failure)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWith(DeleteTaskResult.InFlight)
            }
        }

    internal var actionProcessor =
        ObservableTransformer<TaskDetailAction, TaskDetailResult> { actions ->
            actions.publish { shared ->
                Observable.merge<TaskDetailResult>(
                    shared.ofType(PopulateTaskAction::class.java).compose(populateTaskProcessor),
                    shared.ofType(CompleteTaskAction::class.java).compose(completeTaskProcessor),
                    shared.ofType(ActivateTaskAction::class.java).compose(activateTaskProcessor),
                    shared.ofType(DeleteTaskAction::class.java).compose(deleteTaskProcessor))
                    .mergeWith(
                        shared.filter  {v ->
                            (v !is PopulateTaskAction
                                    && v !is CompleteTaskAction
                                    && v !is ActivateTaskAction
                                    && v !is DeleteTaskAction) }
                .flatMap { w ->
                    Observable.error<TaskDetailResult>(
                        IllegalArgumentException("unknown Action type: " + w)
                    )
                }
            )
        }
    }
}