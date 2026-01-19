package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.GarbageDisposalDto
import dev.cloudants.iulat.lib.services.GarbageDisposalService
import dev.cloudants.iulat.lib.state.GarbageDisposalState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarbageDisposalViewModel @Inject constructor(
    val garbageService: GarbageDisposalService,
) : ViewModel() {

    private val _state = MutableStateFlow(GarbageDisposalState())
    val state: StateFlow<GarbageDisposalState> = _state

    fun fetchAll(currentUserId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = garbageService.getAll(currentUserId)
            _state.value = _state.value.copy(
                items = reports,
                isLoading = false
            )
            Log.e("REP ::", reports.size.toString())
        }
    }

    fun fetchAllGarbageReports() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = garbageService.getAll()
            _state.value = _state.value.copy(
                items = reports ,
                isLoading = false
            )
        }
    }

    fun createGarbageReport(dto: GarbageDisposalDto) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val savedReport = garbageService.create(dto)
                Log.d("GarbageDisposalViewModel", "Garbage report saved: $savedReport")
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
            val report = garbageService.getById(reportId)
            _state.value = _state.value.copy(
                selectedReport = report,
                isLoading = false
            )
        }
    }

    fun updateGarbageReport(dto: GarbageDisposalDto) {
        viewModelScope.launch {
            try {
                val reportId = dto.id
                    ?: throw IllegalArgumentException("Report ID must be provided for update.")
                _state.value = _state.value.copy(isLoading = true)
                val updatedReport = garbageService.update(reportId, dto)
                Log.d("GarbageDisposalViewModel", "Garbage report updated: $updatedReport")
                _state.value = _state.value.copy(isDialogVisible = true, isLoading = false)
            } catch (e: Exception) {
                Log.e("GarbageDisposalViewModel", "Update failed: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}