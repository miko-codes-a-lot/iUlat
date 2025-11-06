package dev.cloudants.iulat.lib.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.intent.LoginIntent
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services_impl.UserServiceImpl
import dev.cloudants.iulat.lib.state.LoginState
import dev.cloudants.iulat.lib.utils.main.MainNav
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userService: UserServiceImpl,
    application: Application

) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.DisplayDialog -> {
                _uiState.value = _uiState.value.copy(isDialogShow = intent.isShow)
            }
            is LoginIntent.ClearErrorMessage -> {
                _uiState.value = _uiState.value.copy(errorMessage = "")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            try {
                val user = userService.login(email, password)
                if (user != null) {
                    with(sharedPreferences.edit()) {
                        putString("logged_in_user_id", user.id)
                        putBoolean("is_admin", user.isAdmin)
                        putBoolean("is_residence", user.isResidence)
                        apply()
                    }
                    delay(500)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        isDialogShow = true,
                        user = user
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = false,
                        isDialogShow = true,
                        errorMessage = "Invalid username or password"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Something went wrong"
                )
            }
        }
    }




    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun getLoggedInUserId(): String? {
        return sharedPreferences.getString("logged_in_user_id", null)
    }

    fun getUserLocal(): UserDto? {
        val userId = getLoggedInUserId()

        if (userId != null){
            return userService.fetchOne(userId)
        }

        return null
    }

    private fun clearUserSession() {
        sharedPreferences.edit().clear().apply()
    }

    fun logout(navController: NavController) {
        clearUserSession()
        navController.navigate(MainNav) {
            popUpTo<MainNav.Login> { inclusive = true }
            launchSingleTop = true
        }
    }

}
