package dev.cloudants.iulat.lib.ui.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import dev.cloudants.iulat.R
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.components.dialog.LoginDialog
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.viewmodels.AccountViewModel
import dev.cloudants.iulat.lib.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun EditAccount(
    navController: NavController, settingType: String, currentDto: UserDto
) {
    val accountViewModel: AccountViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (settingType) {
            "fullName" -> FullNameEdit(currentDto)
            "email" -> EmailEdit(currentDto, accountViewModel)
            "mobileNumber" -> PhoneNumberEdit(currentDto, accountViewModel)
            "password" -> PasswordEdit(navController, currentDto, accountViewModel)
            else -> Text(text = "Invalid setting type")
        }
    }
}


@Composable
fun FullNameEdit(currentDto: UserDto) {
    val viewModel: UserViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    var editFullNameMode by remember { mutableStateOf(false) }
    val fieldLabels = listOf("First Name", "Middle Name", "Last Name")

    var firstNameError: String? by remember { mutableStateOf(null) }
    var middleNameError: String? by remember { mutableStateOf(null) }
    var lastNameError: String? by remember { mutableStateOf(null) }

    val nameRegex = Regex("^[a-zA-Z\\s]*$")

    val fieldStates = remember {
        mutableMapOf(
            "First Name" to mutableStateOf(currentDto.firstName),
            "Middle Name" to mutableStateOf(currentDto.middleName ?: ""),
            "Last Name" to mutableStateOf(currentDto.lastName)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (editFullNameMode) {
                Column {
                    fieldLabels.forEach { label ->
                        val isFieldError = when (label) {
                            "First Name" -> firstNameError != null
                            "Last Name" -> lastNameError != null
                            else -> false
                        }
                        val errorText = when (label) {
                            "First Name" -> firstNameError
                            "Middle Name" -> middleNameError
                            "Last Name" -> lastNameError
                            else -> null
                        }
                        OutlinedTextField(
                            value = fieldStates[label]?.value ?: "",
                            onValueChange = { newValue ->
                                fieldStates[label]?.value = newValue
                                when (label) {
                                    "First Name" -> firstNameError = if (newValue.matches(nameRegex)) null else "No numbers allowed"
                                    "Middle Name" -> middleNameError = if (newValue.isEmpty() || newValue.matches(nameRegex)) null else "No numbers allowed"
                                    "Last Name" -> lastNameError = if (newValue.matches(nameRegex)) null else "No numbers allowed"
                                }
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontFamily = FontFamily.SansSerif,
                                    color = if (isFieldError) Color.Red else Color(0xFF0049AD),
                                    fontSize = 15.sp
                                )
                            },
                            isError = isFieldError,
                            supportingText = {
                                errorText?.let { Text(it, color = Color.Red, fontFamily = FontFamily.SansSerif) }
                            },
                            textStyle = TextStyle(
                                color = Color(0xFF0049AD),
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0049AD),
                                unfocusedBorderColor = Color(0xFF0049AD),
                                cursorColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, color = Color(0xFF0049AD), shape = RoundedCornerShape(5.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Full Name : ",
                                fontSize = 16.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color(0xFF0049AD)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentDto.firstName +" "+ currentDto.middleName +" "+ currentDto.lastName,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color(0xFF0049AD)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 7.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Button(
                onClick = {
                    if (editFullNameMode) {
                        currentDto.firstName = fieldStates["First Name"]?.value ?: ""
                        currentDto.middleName = fieldStates["Middle Name"]?.value ?: ""
                        currentDto.lastName = fieldStates["Last Name"]?.value ?: ""

                        coroutineScope.launch {
                            try {
                                val result = viewModel.updateUser(
                                    currentDto.copy(
                                        firstName = currentDto.firstName,
                                        middleName = currentDto.middleName,
                                        lastName = currentDto.lastName
                                    )
                                )
                                if (result.isSuccess) {
                                    editFullNameMode = false
                                } else {
                                    Log.e("FullNameEdit", "Failed to update fullname")
                                }
                            } catch (error: Exception) {
                                Log.e("FullNameEdit", "Failed to update fullname: ${error.message}")
                            }
                        }
                    }
                    else {
                        editFullNameMode = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(Color(0xFF0049AD))
                    .border(1.dp, color = Color(0xFF0049AD), shape = RoundedCornerShape(5.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                enabled = firstNameError == null && lastNameError == null
            ) {
                Text(
                    text = if (!editFullNameMode) "Change" else "Save",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

@Composable
fun EmailEdit(currentDto: UserDto, accountViewModel: AccountViewModel ) {
    val viewModel: UserViewModel = hiltViewModel()
    val state by accountViewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var editEmail by remember { mutableStateOf(currentDto.email ?: "") }
    var editEmailMode by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    if (state.isDialogVisible) {
        LoginDialog(
            title = "Update Successful",
            label = "Success!",
            message = "Your email has been updated successfully.",
            isLoginSuccessful = true,
            onConfirm = { accountViewModel.hideDialog() },
            onDismiss = { accountViewModel.hideDialog() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (editEmailMode) {
                OutlinedTextField(
                    value = editEmail,
                    onValueChange = { newValue ->
                        editEmail = newValue
                        emailError = if (newValue.isEmpty()) "Email cannot be empty" else null
                    },
                    label = { Text(
                        text = "Email",
                        fontFamily = FontFamily.SansSerif,
                        color = if (emailError != null) Color.Red else Color(0xFF0049AD),
                        fontSize = 15.sp
                    )},
                    isError = emailError != null,
                    textStyle = TextStyle(
                        color = Color(0xFF0049AD),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0049AD),
                        unfocusedBorderColor = Color(0xFF0049AD),
                        cursorColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            color = Color(0xFF0049AD),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    {
                        Text(text = "Email :", fontSize = 16.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF0049AD)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentDto.email ?: "No email",
                            fontSize = 16.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF0049AD)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 7.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Button(
                onClick = {
                    if (editEmailMode) {
                        currentDto.email = editEmail
                        coroutineScope.launch {
                            try {
                                val result = viewModel.updateUser(currentDto.copy(email = editEmail))
                                if (result.isSuccess) {
                                    editEmailMode = false
                                    accountViewModel.showDialog()
                                } else {
                                    Log.e("EmailEdit", "Failed to update email")
                                }
                            } catch (error: Exception) {
                                Log.e("EmailEdit", "Failed to update email: ${error.message}")
                            }
                        }
                    }
                    else {
                        editEmailMode = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(Color(0xFF0049AD))
                    .border(1.dp, color = Color(0xFF0049AD), shape = RoundedCornerShape(5.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                enabled = emailError == null
            ) {
                Text(
                    text = if (!editEmailMode) "Change" else "Save",
                    fontSize = 16.sp, fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

@Composable
fun PhoneNumberEdit(currentDto: UserDto, accountViewModel: AccountViewModel) {
    val viewModel: UserViewModel = hiltViewModel()
    val state by accountViewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var editMobileNumber by remember { mutableStateOf(currentDto.mobileNumber ?: "") }
    var editMobileNumberMode by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }

    val phoneNumberRegex = Regex("^\\d{11}$")

    if (state.isDialogVisible) {
        LoginDialog(
            title = "Update Successful",
            label = "Success!",
            message = "Your mobile number has been updated successfully.",
            isLoginSuccessful = true,
            onConfirm = { accountViewModel.hideDialog() },
            onDismiss = { accountViewModel.hideDialog() }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (editMobileNumberMode) {
                OutlinedTextField(
                    value = editMobileNumber,
                    onValueChange = { newValue ->
                        editMobileNumber = newValue
                        phoneNumberError = if (newValue.matches(phoneNumberRegex)) {
                            null
                        } else {
                            "Phone number must be 11 digits and contain only numbers"
                        }
                    },
                    label = { Text(text = "Mobile Number",
                        fontFamily = FontFamily.SansSerif,
                        color = if (phoneNumberError != null) Color.Red else Color(0xFF0049AD),
                        fontSize = 15.sp)
                    },
                    isError = phoneNumberError != null,
                    textStyle = TextStyle(
                        color = Color(0xFF0049AD),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0049AD),
                        unfocusedBorderColor = Color(0xFF0049AD),
                        cursorColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            color = Color(0xFF0049AD),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    {
                        Text(text = "Mobile Number :",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = (Color(0xFF0049AD))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentDto.mobileNumber ?: "No Mobile Number",
                            fontSize = 16.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF0049AD)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 7.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Button(
                onClick = {
                    if (editMobileNumberMode) {
                        currentDto.mobileNumber = editMobileNumber
                        coroutineScope.launch {
                            try {
                                val result = viewModel.updateUser(
                                    currentDto.copy(
                                        mobileNumber = currentDto.mobileNumber
                                    )
                                )
                                if (result.isSuccess) {
                                    editMobileNumberMode = false
                                    accountViewModel.showDialog()
                                } else {
                                    Log.e("MobileEdit", "Failed to update mobile")
                                }
                            } catch (error: Exception) {
                                Log.e("MobileEdit", "Failed to update mobile: ${error.message}")
                            }
                        }
                    }
                    else{
                        editMobileNumberMode = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(Color(0xFF0049AD))
                    .border(1.dp, color = Color(0xFF0049AD), shape = RoundedCornerShape(5.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                enabled = phoneNumberError == null
            ) {
                Text(
                    text = if (!editMobileNumberMode) "Change" else "Save",
                    fontSize = 16.sp, fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

@Composable
fun PasswordEdit(navController: NavController, currentDto: UserDto, accountViewModel: AccountViewModel) {
    val viewModel: UserViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val state by accountViewModel.state.collectAsState()
    val fieldLabels = listOf("Current Password", "New Password", "Re-Type New Password")
    val fieldStates = remember { fieldLabels.associateWith { mutableStateOf("") } }

    var currentPasswordError: String? by remember { mutableStateOf(null) }
    var newPasswordError: String? by remember { mutableStateOf(null) }

    val passwordVisibilityStates = remember {
        fieldLabels.associateWith { mutableStateOf(false) }.toMutableMap()
    }

    if (state.isDialogVisible) {
        LoginDialog(
            title = "Update Successful",
            label = "Success!",
            message = "Your password has been updated successfully.",
            isLoginSuccessful = true,
            onConfirm = { accountViewModel.hideDialog() },
            onDismiss = { accountViewModel.hideDialog() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        fieldLabels.forEach { label ->
            val passwordVisible = passwordVisibilityStates[label]?.value ?: false
            OutlinedTextField(
                value = fieldStates[label]?.value ?: "",
                onValueChange = { newValue ->
                    fieldStates[label]?.value = newValue
                    if (label == "Current Password") {
                        currentPasswordError = null
                    } else if (label == "New Password" || label == "Re-Type New Password") {
                        newPasswordError = null
                    }
                },
                label = {
                    Text(
                        text = label,
                        color = Color(0xFF0049AD),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibilityStates[label]?.value = !passwordVisible
                    }) {
                        Icon(
                            painter = if (passwordVisible) painterResource(R.drawable.visibilityon) else painterResource(
                                R.drawable.visibility_off
                            ),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF0049AD)
                        )
                    }
                },
                isError = when (label) {
                    "Current Password" -> currentPasswordError != null
                    "New Password", "Re-Type New Password" -> newPasswordError != null
                    else -> false
                },
                textStyle = TextStyle(
                    color = Color(0xFF0049AD),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.SansSerif,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0049AD),
                    unfocusedBorderColor = Color(0xFF0049AD),
                    cursorColor = Color.Gray
                ),
                supportingText = {
                    when (label) {
                        "Current Password" -> currentPasswordError?.let {
                            Text(
                                it,
                                color = Color.Red
                            )
                        }

                        "New Password", "Re-Type New Password" -> newPasswordError?.let {
                            Text(
                                it,
                                color = Color.Red
                            )
                        }

                        else -> Spacer(modifier = Modifier.height(0.dp))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(
            onClick = {
                val currentPassword = fieldStates["Current Password"]?.value?.trim()
                val newPassword = fieldStates["New Password"]?.value?.trim() ?: ""
                val retypePassword = fieldStates["Re-Type New Password"]?.value?.trim() ?: ""
                Log.d("PasswordCheck", "🔐 Entered current password: '$currentPassword'")
                Log.d("PasswordCheck", "🔐 Stored (hashed) password: '${currentDto.password}'")

                when {
                    currentPassword == null || !BCrypt.checkpw(currentPassword, currentDto.password) -> {
                        currentPasswordError = "Current password is incorrect"
                    }
                    newPassword != retypePassword -> {
                        newPasswordError = "New passwords do not match"
                    }
                    else -> {
                        coroutineScope.launch {
                            try {
                                val result = viewModel.updateUser(currentDto.copy(password = newPassword))
                                if (result.isSuccess) {
                                    accountViewModel.showDialog()
                                    navController.navigate(MainNav.Menu) {
                                        popUpTo(0)
                                    }
                                } else {
                                    Log.e("PasswordEdit", "Failed to update password")
                                }
                            } catch (error: Exception) {
                                Log.e("PasswordEdit", "Failed to update password: ${error.message}")
                            }
                        }
                        fieldStates.forEach { (_, state) -> state.value = "" }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(Color(0xFF0049AD))
                .border(
                    2.dp,
                    color = Color(0xFF0049AD),
                    shape = RoundedCornerShape(5.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Save",
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}