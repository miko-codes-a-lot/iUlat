package dev.cloudants.iulat.lib.ui.user

import android.util.Log
import androidx.compose.runtime.*

@Composable
fun CreateAccount() {
    var showForm by remember { mutableStateOf(true) }

    var userDetails by remember {
        mutableStateOf(
            UserSample(
                firstName = "Admin",
                middleName = "Sample",
                lastName = "User",
                address = "123 Admin Street",
                mobileNumber = "09123456789",
                dateOfBirth = "1990-01-01",
                email = "admin@gmail.com"
            )
        )
    }

    if (showForm) {
        // ✅ Show the form first
        UserForm(
            title = "Create Account",
            onSubmit = { user ->
                userDetails = user
                showForm = false
            }
        )
    } else {
        UserPreview(
            title = "Preview Account",
            user = userDetails,
            onSave = {
                Log.d("CreateAccount", "User saved: $userDetails")
                showForm = true
            },
            onCancel = {
                showForm = true
            }
        )
    }
}
