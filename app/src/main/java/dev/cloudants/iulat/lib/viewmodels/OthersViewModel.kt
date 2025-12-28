package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.NoWaterSupplyDto
import dev.cloudants.iulat.lib.models.entities.OthersDto
import dev.cloudants.iulat.lib.services.OthersService
import dev.cloudants.iulat.lib.state.OthersState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersViewModel @Inject constructor(
    val othersService: OthersService
) : ViewModel() {
    private val _state = MutableStateFlow(OthersState())
    val state: StateFlow<OthersState> = _state.asStateFlow()

    fun fetchAll(currentUserId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = othersService.getAll(currentUserId)
            _state.value = _state.value.copy(
                items = reports.sortedByDescending { it.createdAt },
                isLoading = false
            )
        }
    }

    fun fetchAllOthers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = othersService.getAll()
            _state.value = _state.value.copy(
                items = reports.sortedByDescending { it.createdAt },
                isLoading = false
            )
        }
    }

    fun createOthersReport(dto: OthersDto) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val savedReport = othersService.create(dto)
                Log.d("OthersViewModel", "others report saved: $savedReport")
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
            val report = othersService.getById(reportId)
            _state.value = _state.value.copy(
                selectedReport = report,
                isLoading = false
            )
        }
    }

    fun updateOthersReport(dto: OthersDto) {
        viewModelScope.launch {
            try {
                val reportId = dto.id
                    ?: throw IllegalArgumentException("Report ID must be provided for update.")
                _state.value = _state.value.copy(isLoading = true)
                val updatedReport = othersService.update(reportId, dto)
                Log.d("OthersViewModel", "others report updated: $updatedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                Log.e("OthersViewModel", "Update failed: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}