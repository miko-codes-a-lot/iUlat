package dev.cloudants.iulat.lib.ui.user

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.services.UserService
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.UserViewModel

@Composable
fun UserEdit(
    navController: NavController,
    currentUser : UserDto,
    userDto : UserDto,
    userService: UserService = hiltViewModel<UserViewModel>().userService
) {
    val userViewModel: UserViewModel = hiltViewModel()
    var userDetails by remember { mutableStateOf(UserDto()) }
    var showForm by remember { mutableStateOf(true) }

    if (showForm) {
        UserForm(
            title = "Edit Account",
            targetUserDto = userDto,
            currentUser = currentUser,
            onSubmit = { user ->
                userDetails = user
                showForm = false
            },
            navController = navController,
            includePassword = false,
            addressDto = null,
        )
    } else {
        UserPreview(
            user = userDetails,
            title = "Review Changes",
            onSave = { user ->
                val result = userViewModel.updateUser(user)

                if (result.isSuccess) {
                    navController.popBackStack()
                } else {
                    Log.e("micool", "Something went wrong: ${result.exceptionOrNull()}")
                }
            },
            onCancel = {
                showForm = true
            }
        )
    }
}