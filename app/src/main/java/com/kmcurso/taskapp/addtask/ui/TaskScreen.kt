package com.kmcurso.taskapp.addtask.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.kmcurso.taskapp.addtask.ui.model.TaskModel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon


@Composable
fun TasksScreen(taskViewModel: TaskViewModel, innerPadding: PaddingValues) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val showDialog: Boolean by taskViewModel.showDialog.observeAsState(false)
    var taskToDelete by remember { mutableStateOf<TaskModel?>(null) }


    val uiState by produceState<TasksUiState>(
        initialValue = TasksUiState.Loading,
        key1 = lifecycle,
        key2 = taskViewModel,
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            taskViewModel.uiState.collect {
                value = it
            }
        }
    }

    // ✅ Mover estas fuera del when
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<TaskModel?>(null) }

    when (uiState) {
        is TasksUiState.Error -> {}
        TasksUiState.Loading -> {
            CircularProgressIndicator()
        }

        is TasksUiState.Success -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AddTaskDialog(
                    showDialog,
                    onDismiss = { taskViewModel.onDialogClose() },
                    onTaskAdded = { taskViewModel.onTaskCreated(it) }
                )

                if (showEditDialog && selectedTask != null) {
                    EditTaskDialog(
                        show = true,
                        task = selectedTask!!,
                        onDismiss = { showEditDialog = false },
                        onTaskEdited = {
                            taskViewModel.onTaskEdited(it)
                            showEditDialog = false
                        }
                    )
                }

                if (taskToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { taskToDelete = null },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Advertencia",
                                    tint = Color(0xFFFFA000), // color ámbar
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Eliminar tarea")
                            }
                        },
                        text = { Text("¿Estás seguro de que deseas eliminar esta tarea?") },
                        confirmButton = {
                            Button(onClick = {
                                taskToDelete?.let { taskViewModel.onItemRemove(it) }
                                taskToDelete = null
                            }) {
                                Text("Sí")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { taskToDelete = null }) {
                                Text("No")
                            }
                        }
                    )
                }

                FabDialog(Modifier.align(Alignment.BottomEnd), taskViewModel)

                TaskList(
                    tasks = (uiState as TasksUiState.Success).tasks,
                    taskViewModel = taskViewModel,
                    onDoubleTap = {
                        selectedTask = it
                        showEditDialog = true
                    },
                    onLongPress = {
                        taskToDelete = it
                    }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<TaskModel>,
    taskViewModel: TaskViewModel,
    onDoubleTap: (TaskModel) -> Unit,
    onLongPress: (TaskModel) -> Unit
) {
    LazyColumn {
        items(tasks, key = { it.id }) { task ->
            ItemTask(task, taskViewModel, onDoubleTap, onLongPress)
        }
    }
}


@Composable
fun ItemTask(
    taskModel: TaskModel,
    taskViewModel: TaskViewModel,
    onDoubleTap: (TaskModel) -> Unit,
    onLongPress: (TaskModel) -> Unit
)
 {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress(taskModel) },
                    onDoubleTap = { onDoubleTap(taskModel) }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                taskModel.task,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f)
            )
            Checkbox(
                checked = taskModel.selected,
                onCheckedChange = { taskViewModel.onCheckBoxSelected(taskModel) })
        }
    }
}


@Composable
fun FabDialog(modifier: Modifier, taskViewModel: TaskViewModel) {
    FloatingActionButton(
        onClick = {
            taskViewModel.onShowDialogClick()
        },
        modifier = modifier.padding(16.dp)
    ) {
        Icon(Icons.Filled.Add, contentDescription = "")
    }
}

@Composable
fun AddTaskDialog(show: Boolean, onDismiss: () -> Unit, onTaskAdded: (String) -> Unit) {
    var myTask by remember { mutableStateOf("") }

    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    "Añade tu tarea",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(16.dp))
                TextField(
                    value = myTask,
                    onValueChange = { myTask = it },
                    singleLine = false,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    onTaskAdded(myTask)
                    myTask = ""
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Añadir tarea")
                }
            }
        }
    }
}

@Composable
fun EditTaskDialog(
    show: Boolean,
    task: TaskModel,
    onDismiss: () -> Unit,
    onTaskEdited: (TaskModel) -> Unit
) {
    var editedText by remember { mutableStateOf(task.task) }

    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("Editar tarea", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(16.dp))
                TextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    onTaskEdited(task.copy(task = editedText)) // Actualiza solo el texto
                    onDismiss()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Guardar")
                }
            }
        }
    }
}
