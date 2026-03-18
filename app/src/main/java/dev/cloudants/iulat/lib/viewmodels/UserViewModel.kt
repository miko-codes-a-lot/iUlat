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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val userService: UserService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserState())
    val uiState: StateFlow<UserState> = _uiState
    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users: StateFlow<List<UserDto>> = _users
    private val _currentUserState = MutableStateFlow<UserDto?>(null)
    val currentUserState: StateFlow<UserDto?> = _currentUserState

    fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userService.findAll()
            _users.value = result
        }
    }

    fun loadCurrentUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userService.findOne(userId)
            _currentUserState.value = user
        }
    }

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

    fun fetchByEmail(email: String): UserDto? {
        return userService.findByEmail(email)
    }

    fun fetchUserByEmailAndToken(email: String, token: String): UserDto? {
        return userService.fetchEmailAndToken(email, token)
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
                val uniqueZones = zones.distinctBy { "${it.province}-${it.municipality}-${it.barangay}-${it.zone}" }

                val zonesToSave = uniqueZones.filter { address ->
                    !userService.isZoneExisting(
                        province = address.province,
                        municipality = address.municipality,
                        barangay = address.barangay,
                        zone = address.zone
                    )
                }

                if (zonesToSave.isEmpty()) {
                    Log.e("UserViewModel", "⚠️ No new zones to save.")
                    return@launch
                }

                zonesToSave.forEachIndexed { index, address ->
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

                val isSaved = userService.saveZonesToDatabase(zonesToSave)

                if (isSaved) {
                    Log.e("UserViewModel", " Zones successfully saved to database.")
                } else {
                    Log.e("UserViewModel", " Failed to save zones.")
                }

            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to save zones: ${e.message}")
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

    fun updateProfileImage(userId: String, imageBytes: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)

                val currentUser = userService.findOne(userId)
                val updatedUser = currentUser.copy(imageBase64 = base64Image)

                userService.update(userId, updatedUser)
                _currentUserState.value = updatedUser
                loadUsers()
                Log.d("UserViewModel", "Profile image updated successfully")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to update profile image: ${e.message}")
            }
        }
    }

    fun saveValidId(userId: String, imageBytes: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = userService.saveValidId(userId, imageBytes)
                if (result.isSuccess) {
                    _currentUserState.value = result.getOrNull()
                    Log.d("UserViewModel", "Valid ID saved successfully")
                } else {
                    Log.e("UserViewModel", "Failed to save ID: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error in saveValidId: ${e.message}")
            }
        }
    }

    fun saveVoterCertificate(userId: String, imageBytes: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
                val currentUser = userService.findOne(userId)
                val updatedUser = currentUser.copy(voterCertificate = base64Image)

                userService.update(userId, updatedUser)
                _currentUserState.value = updatedUser
                Log.d("UserViewModel", "Voter Certificate updated successfully")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to update Voter Certificate: ${e.message}")
            }
        }
    }

    fun verifyResidentEmail(email: String, onResult: (UserDto?) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {

            val user = withContext(Dispatchers.IO) {
                userService.findByEmail(email)
            }

            if (user != null && user.type == "user" && user.isVerified && user.isResidence) {
                onResult(user)
            } else {
                onResult(null)
            }
        }
    }

}