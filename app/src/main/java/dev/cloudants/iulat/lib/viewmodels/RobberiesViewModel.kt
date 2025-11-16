package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.RoadRepairDto
import dev.cloudants.iulat.lib.models.entities.RobberiesDto
import dev.cloudants.iulat.lib.services.RobberiesService
import dev.cloudants.iulat.lib.state.RoadRepairState
import dev.cloudants.iulat.lib.state.RobberiesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RobberiesViewModel @Inject constructor(
    val robberiesService: RobberiesService
) : ViewModel() {
    private val _state = MutableStateFlow(RobberiesState())
    val state: StateFlow<RobberiesState> = _state.asStateFlow()

    fun fetchAll(currentUserId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = robberiesService.getAll(currentUserId)
            _state.value = _state.value.copy(
                items = reports.sortedByDescending { it.createdAt },
                isLoading = false
            )
        }
    }

    fun createRobberiesReport(dto: RobberiesDto) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val savedReport = robberiesService.create(dto)
                Log.d("RobberiesViewModel", "Robberies report saved: $savedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun dismissDialog() {
        _state.value = _state.value.copy(isDialogVisible = false)
    }

    fun fetchReportById(reportId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val report = robberiesService.getById(reportId)
            _state.value = _state.value.copy(
                selectedReport = report,
                isLoading = false
            )
        }
    }

    fun updateRobberiesReport(dto: RobberiesDto) {
        viewModelScope.launch {
            try {
                val reportId = dto.id
                    ?: throw IllegalArgumentException("Report ID must be provided for update.")
                _state.value = _state.value.copy(isLoading = true)
                val updatedReport = robberiesService.update(reportId, dto)
                Log.d("RobberiesViewModel", "robberies report updated: $updatedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                Log.e("RobberiesViewModel", "Update failed: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}