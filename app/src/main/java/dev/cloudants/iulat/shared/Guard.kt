package dev.cloudants.iulat.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.viewmodels.LoginViewModel

@Composable
fun Guard(navController: NavController, render: @Composable (currentUser: UserDto) -> Unit) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val userId = rememberSaveable { mutableStateOf(loginViewModel.getLoggedInUserId()) }

    if (userId.value != null) {
        val currentUser = loginViewModel.getUserLocal()
        if (currentUser != null) {
            return render(currentUser)
        }
    }
    loginViewModel.logout(navController)
}