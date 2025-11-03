package dev.cloudants.iulat.lib.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.components.context.UserSession
import dev.cloudants.iulat.lib.intent.LoginIntent
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.state.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onIntent(intent: LoginIntent.DisplayDialog) {
        when (intent) {
            is LoginIntent.DisplayDialog -> {
                _uiState.value = _uiState.value.copy(isDialogShow = intent.isShow)
            }
        }
    }

//    fun login(email: String, password: String) {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true)
//            if (email == "sample@gmail.com" && password == "password") {
//                onIntent(LoginIntent.DisplayDialog(true))
//            } else {
//                onIntent(LoginIntent.DisplayDialog(true))
//            }
//            _uiState.value = _uiState.value.copy(isLoading = false)
//        }
//    }
    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val user = when {
                email == "admin@gmail.com" && password == "password" -> {
                    UserDto(
                        id = "1",
                        username = "admin",
                        password = null,
                        firstName = "Admin",
                        middleName = null,
                        lastName = "User",
                        email = "admin@gmail.com",
                        mobileNumber = "09123456789",
                        gender = "Male",
                        address = AddressDto("Street 1", "City", "Province"),
                        type = "Admin",
                        role = "Admin"
                    )
                }

                email == "residence@gmail.com" && password == "password" -> {
                    UserDto(
                        id = "2",
                        username = "normal",
                        password = null,
                        firstName = "Normal",
                        middleName = null,
                        lastName = "User",
                        email = "residence@gmail.com",
                        mobileNumber = "09999999999",
                        gender = "Female",
                        address = AddressDto("Street 2", "City", "Province"),
                        type = "Residence",
                        role = "Residence"
                    )
                }

                else -> null
            }

            if (user != null) {
                UserSession.saveUserRole(context, user.role)

                val isAdmin = user.role == "Admin"
                val isResidence = user.role == "Residence"

                val defaultRoute = when {
                    isAdmin -> MODULE.DASHBOARD
                    isResidence -> MODULE.RESIDENCEDASHBOARD
                    else -> MODULE.DASHBOARD
                }

                _uiState.value = _uiState.value.copy(
                    user = user,
                    isDialogShow = true,
                    isLoginSuccessful = true,
                    isLoading = false,
                    errorMessage = "",
                    route = defaultRoute
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isDialogShow = true,
                    isLoginSuccessful = false,
                    isLoading = false,
                    errorMessage = "Invalid email or password"
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


}
