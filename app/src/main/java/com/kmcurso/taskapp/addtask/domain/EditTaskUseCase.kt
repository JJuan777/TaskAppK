package com.kmcurso.taskapp.addtask.domain

import com.kmcurso.taskapp.addtask.data.TaskRepository
import com.kmcurso.taskapp.addtask.ui.model.TaskModel
import javax.inject.Inject

class EditTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: TaskModel) {
        repository.update(task) // ← Usa tu update() ya existente
    }
}
