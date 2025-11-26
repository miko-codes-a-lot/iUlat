package dev.cloudants.iulat.lib.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.MyLogsDto
import dev.cloudants.iulat.lib.services.MyLogsService
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyLogsViewModel @Inject constructor(
    val logsService: MyLogsService
) : ViewModel() {
    val logsList: SnapshotStateList<MyLogsDto> = mutableStateListOf()
    val selectedLog = mutableStateOf<MyLogsDto?>(null)

    fun loadLogs() {
        viewModelScope.launch {
            val logs = logsService.fetchAll()
            logsList.clear()
            logsList.addAll(logs)
        }
    }

    fun createLog() {
        viewModelScope.launch {

            logsService.logs()
            loadLogs()
        }
    }

    fun getLog(id: String) {
        selectedLog.value = logsService.fetchOne(id)
    }
}