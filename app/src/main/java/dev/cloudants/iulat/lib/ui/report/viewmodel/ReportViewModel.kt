package dev.cloudants.iulat.lib.ui.report.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.ui.report.intent.ReportIntent
import dev.cloudants.iulat.lib.ui.report.state.ReportState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state

    fun onIntent(intent: ReportIntent) {
        when (intent) {
            is ReportIntent.SubmitReport -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isDialogVisible = true)
                }
            }

            ReportIntent.DismissDialog -> {
                _state.value = _state.value.copy(isDialogVisible = false)
            }
        }
    }
}