package com.kmcurso.taskapp.addtask.ui

import com.kmcurso.taskapp.addtask.ui.model.TaskModel

interface TasksUiState {
    object Loading:TasksUiState
    data class Error(val throwable: Throwable): TasksUiState
    data class Success(val tasks:List<TaskModel>) :TasksUiState
}