package dev.cloudants.iulat.lib.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.state.AccountState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor () : ViewModel() {
    private val _state = MutableStateFlow(AccountState())
    val state: StateFlow<AccountState> = _state

    fun showDialog() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDialogVisible = true)
        }
    }

    fun hideDialog() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDialogVisible = false)
        }
    }
}
