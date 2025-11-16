package dev.cloudants.iulat.lib.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.lite.MutableDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.intent.UserIntent
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.AddressService
import dev.cloudants.iulat.lib.services.UserService
import dev.cloudants.iulat.lib.state.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val userService: UserService
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

    fun fetchAllUsers(): List<UserDto> {
        return  this.userService.findAll();
    }

    fun fetchUser(userId: String): UserDto {
        return this.userService.findOne(userId)
    }

    fun updateUser(userDto: UserDto): Result<UserDto> {
        return try {
            if (userDto.id?.isBlank()!!) {
                Result.failure(IllegalArgumentException("User DTO must have an ID for update."))
            } else {
                val updatedUser = userService.update(userDto.id, userDto)
                Result.success(updatedUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun saveZonesToDatabase(zones: List<AddressDto>) {
        viewModelScope.launch {
            try {
                zones.forEachIndexed { index, address ->
                    Log.d(
                        "UserViewModel",
                        """
                    [Zone #${index + 1}]
                    Province: ${address.province}
                    Municipality: ${address.municipality}
                    Barangay: ${address.barangay}
                    Zone: ${address.zone}
                    Latitude: ${address.latitude}
                    Longitude: ${address.longitude}
                    """.trimIndent()
                    )
                }

                val isSaved = userService.saveZonesToDatabase(zones)

                if (isSaved) {
                    Log.e("UserViewModel", "✅ Zones successfully saved to database.")
                } else {
                    Log.e("UserViewModel", "❌ Failed to save zones.")
                }

            } catch (e: Exception) {
                Log.e("UserViewModel", "⚠️ Failed to save zones: ${e.message}")
            }
        }
    }

    fun createAdmin() {
        viewModelScope.launch {
            try {
                val plainPassword = "password"
                val hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt())

                val admin = UserDto(
                    username = "admin",
                    password = hashedPassword,
                    firstName = "System",
                    lastName = "Administrator",
                    email = "admin@gmail.com",
                    gender = "N/A"
                )
                val existingAdmin = userService.findByEmail("admin@gmail.com")
                if (existingAdmin == null) {
                    val createdAdmin = userService.createAdminUser(admin)
                    Log.e("UserViewModel", "Created admin: ${createdAdmin.email}")
                } else {
                    Log.e("UserViewModel", "Admin already exists: ${existingAdmin.email}")
                }

            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to create admin: ${e.message}")
            }
        }
    }

}