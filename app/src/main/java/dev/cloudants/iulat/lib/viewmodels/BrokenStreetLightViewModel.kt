package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.BrokenStreetlightsDto
import dev.cloudants.iulat.lib.services.BrokenStreetLightsService
import dev.cloudants.iulat.lib.state.BrokenStreetLightState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrokenStreetLightViewModel @Inject constructor(
    val brokenStreetLightsService: BrokenStreetLightsService,
) : ViewModel() {
    private val _state = MutableStateFlow(BrokenStreetLightState())
    val state: StateFlow<BrokenStreetLightState> = _state

    fun fetchAll(currentUserId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = brokenStreetLightsService.getAll(currentUserId)
            _state.value = _state.value.copy(
                items = reports,
                isLoading = false
            )
        }
    }

    fun fetchAllBrokenStreet() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = brokenStreetLightsService.getAll()
            _state.value = _state.value.copy(
                items = reports,
                isLoading = false
            )
        }
    }

    fun createBrokenLightReport(dto: BrokenStreetlightsDto) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val savedReport = brokenStreetLightsService.create(dto)
                Log.d("BrokenStreetLightViewModel", "Broken Lights report saved: $savedReport")
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
            val report = brokenStreetLightsService.getById(reportId)
            _state.value = _state.value.copy(
                selectedReport = report,
                isLoading = false
            )
        }
    }

    fun updateBrokenLightReport(dto: BrokenStreetlightsDto) {
        viewModelScope.launch {
            try {
                val reportId = dto.id
                    ?: throw IllegalArgumentException("Report ID must be provided for update.")
                _state.value = _state.value.copy(isLoading = true)
                val updatedReport = brokenStreetLightsService.update(reportId, dto)
                Log.d("BrokenStreetLightViewModel", "broken street lights report updated: $updatedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                Log.e("BrokenStreetLightViewModel", "Update failed: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}