package com.kmcurso.taskapp.addtask.domain

import com.kmcurso.taskapp.addtask.data.TaskRepository
import com.kmcurso.taskapp.addtask.ui.model.TaskModel
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskModel: TaskModel){
        taskRepository.update(taskModel)
    }
}