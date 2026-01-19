package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.VehicleCrashDto
import dev.cloudants.iulat.lib.services.VehicleCrashService
import dev.cloudants.iulat.lib.state.VehicleCrashState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleCrashViewModel @Inject constructor(
    val vehicleCrashService: VehicleCrashService
) : ViewModel() {
    private val _state = MutableStateFlow(VehicleCrashState())
    val state: StateFlow<VehicleCrashState> = _state.asStateFlow()

    fun fetchAll(currentUserId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = vehicleCrashService.getAll(currentUserId)
            _state.value = _state.value.copy(
                items = reports,
                isLoading = false
            )
        }
    }

    fun fetchAllVehicleCrash() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = vehicleCrashService.getAll()
            _state.value = _state.value.copy(
                items = reports,
                isLoading = false
            )
        }
    }

    fun createVehicleReport(dto: VehicleCrashDto) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val savedReport = vehicleCrashService.create(dto)
                Log.d("VehicleCrashViewModel", "Vehicle report saved: $savedReport")
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
            val report = vehicleCrashService.getById(reportId)
            _state.value = _state.value.copy(
                selectedReport = report,
                isLoading = false
            )
        }
    }

    fun updateVehicleReport(dto: VehicleCrashDto) {
        viewModelScope.launch {
            try {
                val reportId = dto.id
                    ?: throw IllegalArgumentException("Report ID must be provided for update.")
                _state.value = _state.value.copy(isLoading = true)
                val updatedReport = vehicleCrashService.update(reportId, dto)
                Log.d("VehicleCrashViewModel", "vehicle report updated: $updatedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                Log.e("VehicleCrashViewModel", "Update failed: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}