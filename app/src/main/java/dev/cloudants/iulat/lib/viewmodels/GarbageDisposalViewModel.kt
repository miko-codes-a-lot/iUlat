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
    val garbageService: GarbageDisposalService
) : ViewModel() {

    private val _state = MutableStateFlow(GarbageDisposalState())
    val state: StateFlow<GarbageDisposalState> = _state

    fun fetchAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val reports = garbageService.getAll()
            _state.value = _state.value.copy(items = reports, isLoading = false)
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
}