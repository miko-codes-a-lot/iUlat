package dev.cloudants.iulat.lib.state

import dev.cloudants.iulat.lib.models.entities.UserDto

data class LoginState(
    var email: String = "",
    var password: String = "",
    var isPasswordVisible: Boolean = false,
    var isLoading: Boolean = false,
    var errorMessage: String = "",
    val route: String? = null ,
    var isDialogShow: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val user: UserDto? = null

)
