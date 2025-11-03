package dev.cloudants.iulat.lib.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.viewmodels.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.components.button.CustomButton
import dev.cloudants.iulat.lib.components.dialog.LoginDialog
import dev.cloudants.iulat.lib.intent.LoginIntent
import dev.cloudants.iulat.lib.utils.main.MainNav
@Composable
fun Login(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    val state by loginViewModel.uiState.collectAsState()
    val isLoading = state.isLoading
    val isLoginSuccessful = state.isLoginSuccessful
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Login Image",
            modifier = Modifier.width(200.dp).height(190.dp)
        )

        InputField(
            value = state.email,
            onValueChange = { loginViewModel.onEmailChanged(it) },
            label = "Email",
            isPasswordField = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = state.password,
            onValueChange = { loginViewModel.onPasswordChanged(it) },
            label = "Password",
            isPasswordField = true,
            isPasswordVisible = state.isPasswordVisible,
            onPasswordVisibilityToggle = { loginViewModel.togglePasswordVisibility() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            text = "Login",
            onClick = {
                loginViewModel.login(state.email, state.password, context = navController.context)
            }
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        if (state.errorMessage.isNotEmpty()) {
            Text(
                text = state.errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextButton(onClick = {
        }) {
            Text(
                text = "Forgot password?",
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Red
            )
        }
    }

    if (state.isDialogShow) {
        LoginDialog(
            title = "Login Status",
            label = if (state.isLoginSuccessful) "Login Successful!" else "Invalid credentials",
            message = if (state.isLoginSuccessful) "Welcome back!" else "Please try again.",
            onDismiss = { loginViewModel.onIntent(LoginIntent.DisplayDialog(false)) },
            onConfirm = {
                loginViewModel.onIntent(LoginIntent.DisplayDialog(false))
                coroutineScope.launch {
                    delay(500)
                    val currentUser = state.user
                    if (state.isLoginSuccessful && currentUser != null) {
                        val destinationRoute = state.route

                        navController.navigate(MainNav.Menu) {
                            popUpTo(MainNav.Login) { inclusive = true }
                        }
                    }
                }
            },
            isLoginSuccessful = state.isLoginSuccessful
        )
    }
}



@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordField: Boolean,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, fontFamily = FontFamily.SansSerif, color = Color(0xFF0049AD))
        },
        textStyle = TextStyle(
            color = Color(0xFF0049AD),
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = if (label == "Email") R.drawable.email else R.drawable.lock),
                contentDescription = null,
                tint = Color(0xFF0049AD)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 4f
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = Color(0xFF0049AD),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.Gray
        ),
        visualTransformation = if (isPasswordField && !isPasswordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = {
            if (isPasswordField && onPasswordVisibilityToggle != null) {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.visibilityon else R.drawable.visibility_off
                        ),
                        tint = Color(0xFF0049AD),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

