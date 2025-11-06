package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.UserDto

data class UserState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val createdUser: UserDto? = null
)