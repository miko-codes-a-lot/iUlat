package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.intent.UserIntent
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.UserService
import dev.cloudants.iulat.lib.state.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserState())
    val uiState: StateFlow<UserState> = _uiState

    fun onIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.CreateUser -> createUser(intent.user)
            is UserIntent.ClearState -> clearState()
        }
    }

    private fun createUser(user: UserDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val createdUser = userService.create(user)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    createdUser = createdUser
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to create user."
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = UserState()
    }
}