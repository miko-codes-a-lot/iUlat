package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto
import dev.cloudants.iulat.lib.models.entities.PublicDisturbanceDto
import dev.cloudants.iulat.lib.services.PublicDisturbanceService
import dev.cloudants.iulat.lib.state.BrokenStreetLightState
import dev.cloudants.iulat.lib.state.PublicDisturbanceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicDisturbanceViewModel @Inject constructor(
    val publicDisturbanceService: PublicDisturbanceService
) : ViewModel() {
    private val _state = MutableStateFlow(PublicDisturbanceState())
    val state: StateFlow<PublicDisturbanceState> = _state.asStateFlow()

    fun fetchAll(currentUserId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = publicDisturbanceService.getAll(currentUserId)
            _state.value = _state.value.copy(
                items = reports.sortedByDescending { it.createdAt },
                isLoading = false
            )
        }
    }

    fun fetchAllPublicDisturbance() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = publicDisturbanceService.getAll()
            _state.value = _state.value.copy(
                items = reports.sortedByDescending { it.createdAt },
                isLoading = false
            )
        }
    }

    fun createPublicDisturbanceReport(dto: PublicDisturbanceDto) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val savedReport = publicDisturbanceService.create(dto)
                Log.d("PublicDisturbanceViewModel", "Public Disturbance report saved: $savedReport")
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
            val report = publicDisturbanceService.getById(reportId)
            _state.value = _state.value.copy(
                selectedReport = report,
                isLoading = false
            )
        }
    }

    fun updatePublicDisturbanceReport(dto: PublicDisturbanceDto) {
        viewModelScope.launch {
            try {
                val reportId = dto.id
                    ?: throw IllegalArgumentException("Report ID must be provided for update.")
                _state.value = _state.value.copy(isLoading = true)
                val updatedReport = publicDisturbanceService.update(reportId, dto)
                Log.d("PublicDisturbanceViewModel", "public disturbance report updated: $updatedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                Log.e("PublicDisturbanceViewModel", "Update failed: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}