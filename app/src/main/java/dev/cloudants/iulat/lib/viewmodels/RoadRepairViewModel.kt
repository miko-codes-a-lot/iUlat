package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto
import dev.cloudants.iulat.lib.models.entities.RoadRepairDto
import dev.cloudants.iulat.lib.services.RoadRepairService
import dev.cloudants.iulat.lib.state.PublicDisturbanceState
import dev.cloudants.iulat.lib.state.RoadRepairState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoadRepairViewModel @Inject constructor(
    val roadRepairService: RoadRepairService
) : ViewModel() {
    private val _state = MutableStateFlow(RoadRepairState())
    val state: StateFlow<RoadRepairState> = _state.asStateFlow()

    fun fetchAll(currentUserId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = roadRepairService.getAll(currentUserId)
            _state.value = _state.value.copy(
                items = reports,
                isLoading = false
            )
        }
    }

    fun fetchAllRoadRepair() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = roadRepairService.getAll()
            _state.value = _state.value.copy(
                items = reports,
                isLoading = false
            )
        }
    }

    fun createRoadRepairReport(dto: RoadRepairDto) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val savedReport = roadRepairService.create(dto)
                Log.d("RoadRepairViewModel", "Road Repair report saved: $savedReport")
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
            val report = roadRepairService.getById(reportId)
            _state.value = _state.value.copy(
                selectedReport = report,
                isLoading = false
            )
        }
    }

    fun updateRoadRepairReport(dto: RoadRepairDto) {
        viewModelScope.launch {
            try {
                val reportId = dto.id
                    ?: throw IllegalArgumentException("Report ID must be provided for update.")
                _state.value = _state.value.copy(isLoading = true)
                val updatedReport = roadRepairService.update(reportId, dto)
                Log.d("RoadRepairViewModel", "road repair report updated: $updatedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                Log.e("RoadRepairViewModel", "Update failed: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}