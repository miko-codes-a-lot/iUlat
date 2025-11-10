package dev.cloudants.iulat.lib.ui.user

import android.util.Log
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.intent.UserIntent
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.viewmodels.UserViewModel
@Composable
fun CreateAccount(
    navController: NavController,
    currentUser: UserDto,
    addressDto: AddressDto? = null,
    viewModel: UserViewModel = hiltViewModel()
) {
    var showForm by remember { mutableStateOf(true) }
    var userDetails by remember { mutableStateOf<UserDto?>(null) }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        when {
            state.isLoading -> Log.d("CreateAccount", "Saving user...")
            state.isSuccess -> Log.d("CreateAccount", "✅ User successfully saved to DB")
            state.errorMessage != null -> Log.e("CreateAccount", "❌ Failed to save user: ${state.errorMessage}")
        }
    }

    if (showForm) {
        UserForm(
            title = "Create Account",
            currentUser = currentUser,
            onSubmit = { user ->
                userDetails = user
                showForm = false
                Log.d("CreateAccount", "User form submitted: $user")
            },
            navController = navController,
            addressDto = addressDto,
        )
    } else {
        userDetails?.let { user ->
            UserPreview(
                title = "Preview Account",
                user = user,
                onSave = {
                    Log.d("CreateAccount", "Attempting to save user to DB...")
                    viewModel.onIntent(UserIntent.CreateUser(user))
                    showForm = true
                },
                onCancel = {
                    Log.d("CreateAccount", "Cancelled user creation")
                    showForm = true
                }
            )
        }
    }
}
