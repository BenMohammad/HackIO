package com.benmohammad.hackio.addedittask

import com.benmohammad.hackio.addedittask.AddEditTaskAction.*
import com.benmohammad.hackio.addedittask.AddEditTaskResult.*
import com.benmohammad.hackio.data.Task
import com.benmohammad.hackio.data.source.TasksRepository
import com.benmohammad.hackio.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.lang.IllegalArgumentException

class AddEditTaskActionProcessorHolder(
    private val tasksRepository: TasksRepository,
    private val schedulerProvider: BaseSchedulerProvider
) {

    private val populateTaskProcessor =
        ObservableTransformer<PopulateTaskAction, PopulateTaskResult> { actions ->
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

    private val createTaskProcessor =
        ObservableTransformer<CreateTaskAction, CreateTaskResult> { actions ->
            actions
                .map{action -> Task(title = action.title, description = action.description)}
                .publish { task ->
                    Observable.merge(
                        task.filter(Task::empty).map { CreateTaskResult.Empty },
                        task.filter{!it.empty}.flatMap {
                            tasksRepository.saveTask(it).andThen(Observable.just(CreateTaskResult.Success))
                        }
                    )
                }
        }


    private val updateTaskProcessor =
        ObservableTransformer<UpdateTaskAction, UpdateTaskResult> { actions ->
            actions.flatMap { action ->
                tasksRepository.saveTask(
                    Task(title = action.title, description = action.description, id = action.taskId)
                ).andThen(Observable.just(UpdateTaskResult))
            }
        }

    internal var actionProcessor =
        ObservableTransformer<AddEditTaskAction, AddEditTaskResult> { actions ->
            actions.publish { shared ->
                Observable.merge<AddEditTaskResult>(
                    shared.ofType(AddEditTaskAction.PopulateTaskAction::class.java).compose(populateTaskProcessor),
                    shared.ofType(AddEditTaskAction.CreateTaskAction::class.java).compose(createTaskProcessor),
                    shared.ofType(AddEditTaskAction.UpdateTaskAction::class.java).compose(updateTaskProcessor)
                ).mergeWith(
                    shared.filter { v ->
                        v !is AddEditTaskAction.PopulateTaskAction &&
                                v !is AddEditTaskAction.CreateTaskAction &&
                                v !is AddEditTaskAction.UpdateTaskAction
                    }
                        .flatMap { w ->
                            Observable.error<AddEditTaskResult>(
                                IllegalArgumentException("Unknown Action type:  $w")
                            )
                        }
                )
            }
        }



}