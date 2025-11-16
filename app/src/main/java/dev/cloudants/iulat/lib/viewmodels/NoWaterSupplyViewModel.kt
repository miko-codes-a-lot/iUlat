package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto
import dev.cloudants.iulat.lib.services.NoWaterSupplyService
import dev.cloudants.iulat.lib.state.NoWaterSupplyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoWaterSupplyViewModel @Inject constructor(
    val noWaterSupplyService: NoWaterSupplyService
) : ViewModel() {
    private val _state = MutableStateFlow(NoWaterSupplyState())
    val state: StateFlow<NoWaterSupplyState> = _state.asStateFlow()

    fun fetchAll(currentUserId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = noWaterSupplyService.getAll(currentUserId)
            _state.value = _state.value.copy(
                items = reports.sortedByDescending { it.createdAt },
                isLoading = false
            )
        }
    }

    fun createNoWaterSupplyReport(dto: NoWaterSupplyDto) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val savedReport = noWaterSupplyService.create(dto)
                Log.d("NoWaterSupplyViewModel", "no water supply report saved: $savedReport")
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
            val report = noWaterSupplyService.getById(reportId)
            _state.value = _state.value.copy(
                selectedReport = report,
                isLoading = false
            )
        }
    }

    fun updateNoWaterSupplyReport(dto: NoWaterSupplyDto) {
        viewModelScope.launch {
            try {
                val reportId = dto.id
                    ?: throw IllegalArgumentException("Report ID must be provided for update.")
                _state.value = _state.value.copy(isLoading = true)
                val updatedReport = noWaterSupplyService.update(reportId, dto)
                Log.d("NoWaterSupplyViewModel", "no water supply report updated: $updatedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                Log.e("NoWaterSupplyViewModel", "Update failed: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}