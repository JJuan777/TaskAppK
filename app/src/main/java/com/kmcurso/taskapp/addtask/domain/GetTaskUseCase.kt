package com.kmcurso.taskapp.addtask.domain

import com.kmcurso.taskapp.addtask.data.TaskRepository
import com.kmcurso.taskapp.addtask.ui.model.TaskModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    operator fun invoke():Flow<List<TaskModel>> = taskRepository.tasks
}