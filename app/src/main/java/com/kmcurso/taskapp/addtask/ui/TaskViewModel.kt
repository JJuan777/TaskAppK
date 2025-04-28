package com.kmcurso.taskapp.addtask.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmcurso.taskapp.addtask.domain.AddTaskUseCase
import com.kmcurso.taskapp.addtask.domain.DeleteTaskUseCase
import com.kmcurso.taskapp.addtask.domain.EditTaskUseCase
import com.kmcurso.taskapp.addtask.domain.GetTaskUseCase
import com.kmcurso.taskapp.addtask.domain.UpdateTaskUseCase
import com.kmcurso.taskapp.addtask.ui.TasksUiState.*
import com.kmcurso.taskapp.addtask.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val editTaskUseCase: EditTaskUseCase,
    getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    fun onTaskEdited(task: TaskModel) {
        viewModelScope.launch {
            editTaskUseCase(task)
        }
    }

    val uiState: StateFlow<TasksUiState> = getTaskUseCase()
        .onEach { list -> Log.d("TaskViewModel", "Tasks updated: ${list.size}") }
        .map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)


    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

//    private val _tasks = mutableStateListOf<TaskModel>()
//    val task: List<TaskModel> = _tasks

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(task: String) {
        _showDialog.value = false
        Log.d("TaskViewModel", "Creating task: $task")

        viewModelScope.launch {
            Log.d("TaskViewModel", "Inserting task...")
            addTaskUseCase(TaskModel(task = task))
            Log.d("TaskViewModel", "Task inserted.")
        }

    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onCheckBoxSelected(taskModel: TaskModel) {
        //ACTUALIZ XHECK
//        val index = _tasks.indexOf(taskModel)
//        _tasks[index] = _tasks[index].let {
//            it.copy(selected = !it.selected)
//        }
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(selected = !taskModel.selected))
        }

    }

    fun onItemRemove(taskModel: TaskModel) {
    viewModelScope.launch {
        deleteTaskUseCase(taskModel)
    }
    }
}