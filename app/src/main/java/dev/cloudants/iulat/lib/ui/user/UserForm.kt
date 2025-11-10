package dev.cloudants.iulat.lib.ui.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip 
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale 
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.components.button.CustomButton
import dev.cloudants.iulat.lib.intent.UserIntent
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserForm(
    title: String,
    targetUserDto: UserDto? = null,
    currentUser: UserDto,
    onSubmit: (UserDto) -> Unit,
    includePassword: Boolean = true,
    navController: NavController,
    addressDto: AddressDto?,
    viewModel: UserViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val (chosenRole, setChosenRole) = remember {
        mutableStateOf(
            when {
                targetUserDto?.isAdmin == true -> "Admin"
                targetUserDto?.isResidence == true -> "Residence"
                else -> "Residence"
            }
        )
    }
    var selectedIdUri by remember { mutableStateOf<Uri?>(null) }

    val listOfLabel = mutableListOf(
        "First Name", "Middle Name", "Last Name", "Date Of Birth",
        "Address", "Mobile Number", "Email"
    )

    if (includePassword || targetUserDto != null) {
        listOfLabel.add("Password")
    }

    val statesValue = remember(targetUserDto) {
        listOfLabel.associateWith { label ->
            mutableStateOf(
                when (label) {
                    "First Name" -> targetUserDto?.firstName ?: ""
                    "Middle Name" -> targetUserDto?.middleName ?: ""
                    "Last Name" -> targetUserDto?.lastName ?: ""
                    "Date Of Birth" -> targetUserDto?.dateOfBirth ?: ""
                    "Address" -> targetUserDto?.address?.province ?: ""
                    "Mobile Number" -> targetUserDto?.mobileNumber ?: ""
                    "Email" -> targetUserDto?.email ?: ""
                    "Password" -> targetUserDto?.password ?: ""
                    else -> ""
                }
            )
        }
    }

    LaunchedEffect(targetUserDto) {
        targetUserDto?.let {
            statesValue["First Name"]?.value = it.firstName
            statesValue["Middle Name"]?.value = it.middleName ?: ""
            statesValue["Last Name"]?.value = it.lastName
            statesValue["Date Of Birth"]?.value = it.dateOfBirth
            statesValue["Address"]?.value = it.address?.province ?: ""
            statesValue["Mobile Number"]?.value = it.mobileNumber ?: ""
            statesValue["Email"]?.value = it.email
            statesValue["Password"]?.value = it.password
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Spacer(modifier = Modifier.height(50.dp)) }

        item {
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                modifier = Modifier.offset(y = (-6).dp)
            )
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                listOfLabel.forEach { label ->
                    when (label) {
                        "Date Of Birth" -> {
                            DatePickerField(
                                label = label,
                                dateValue = statesValue[label]?.value ?: "",
                                onDateChange = { selectedDate ->
                                    statesValue[label]?.value = selectedDate
                                }
                            )
                        }

                        else -> {
                            TextFieldContainer(
                                textFieldLabel = label,
                                textFieldValue = statesValue[label]?.value ?: "",
                                onValueChange = { newValue ->
                                    statesValue[label]?.value = newValue
                                }
                            )
                        }
                    }
                }
            }
        }
        if (currentUser.isAdmin) {
            item {
                Text(
                    text = "Select Role:",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Admin", "Residence").forEach { role ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = chosenRole == role,
                                onClick = { setChosenRole(role) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.Blue
                                )
                            )
                            Text(role)
                        }
                    }
                }
            }
        }

        item {
            UploadIdUI(
                existingImageUrl = targetUserDto?.validId,
                onImageSelected = { uri ->
                    selectedIdUri = uri
                }
            )
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
        item {
                Spacer(modifier = Modifier.height(24.dp))
            when {
                state.isLoading -> CircularProgressIndicator()
                else -> CustomButton(
                    text = "Submit",
                    onClick = {
                        val user = UserDto(
                            id =  targetUserDto?.id,
                            username = statesValue["Email"]?.value ?: "",
                            password = statesValue["Password"]?.value ?: "",
                            firstName = statesValue["First Name"]?.value ?: "",
                            middleName = statesValue["Middle Name"]?.value,
                            lastName = statesValue["Last Name"]?.value ?: "",
                            email = statesValue["Email"]?.value ?: "",
                            mobileNumber = statesValue["Mobile Number"]?.value,
                            dateOfBirth = statesValue["Date Of Birth"]?.value ?: "",
                            gender = "Unspecified",
                            address = AddressDto(
                                province = statesValue["Address"]?.value ?: "",
                                municipality = "",
                                barangay = ""
                            ),
                            type = "user",
                            isAdmin = chosenRole == "Admin",
                            isResidence = chosenRole == "Residence",
                            validId = selectedIdUri?.toString() ?: targetUserDto?.validId
                        )

                        onSubmit(user)
                    }
                )
            }

            state.errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (state.isSuccess) {
                Text(
                    text = "User created successfully!",
                    color = Color(0xFF0049AD),
                    modifier = Modifier.padding(top = 8.dp)
                )
                LaunchedEffect(Unit) {
                    delay(1500)
                    viewModel.onIntent(UserIntent.ClearState)
                    navController.popBackStack()
                }
            }
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

@Composable
fun UploadIdUI(
    existingImageUrl: String? = null,
    onImageSelected: (Uri?) -> Unit
) {
    var selectedImgUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImgUri = uri
            onImageSelected(uri)
        }
    )
    val displayImage: Any? = selectedImgUri ?: existingImageUrl

    Box(
        Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp, max = 220.dp)
                .clip(RectangleShape)
                .background(if (displayImage != null) Color.White else Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (displayImage != null) {
                AsyncImage(
                    model = displayImage,
                    contentDescription = "Valid ID",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )

            } else {
                Text(
                    "Tap to Upload ID",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TextFieldContainer(
    textFieldLabel: String,
    textFieldValue: String,
    onValueChange: (String) -> Unit,
) {
    val isPasswordField = textFieldLabel == "Password"
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "$textFieldLabel:",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            fontSize = 17.sp
        )
        TextField(
            value = textFieldValue,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            visualTransformation = if (isPasswordField && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = {
                if (isPasswordField) {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (isPasswordVisible) R.drawable.visibilityon else R.drawable.visibility_off
                            ),
                            contentDescription = null
                        )
                    }
                }
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Gray,
            )
        )
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun DatePickerField(
    label: String, dateValue: String,
    onDateChange: (String) -> Unit,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    LaunchedEffect(dateValue) {
        try {
            val parsedDate = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            ).parse(dateValue)
            parsedDate?.let {
                calendar.time = it
            }
        } catch (_: Exception) {
        }
    }
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val isoFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                isoFormat.timeZone = TimeZone.getTimeZone("UTC")
                val dateISO = isoFormat.format(calendar.time)
                onDateChange(dateISO)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val displayDate = remember(dateValue) {
        if (dateValue.isBlank()) {
            "Select Date"
        } else {
            val possibleFormats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd"
            )

            var parsedDate: Date? = null
            for (pattern in possibleFormats) {
                try {
                    parsedDate = SimpleDateFormat(pattern, Locale.getDefault()).parse(dateValue)
                    if (parsedDate != null) break
                } catch (_: Exception) {}
            }
            parsedDate?.let { displayFormat.format(it) } ?: "Select Date"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 4.dp)
            .padding(top = 8.dp)
            .clickable {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        val isoFormat = SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            Locale.getDefault()
                        )
                        isoFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val dateISO = isoFormat.format(calendar.time)
                        onDateChange(dateISO)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.width(18.dp))

            Icon(
                painter = painterResource(id = R.drawable.calendar_icon),
                contentDescription = "Calendar Icon",
                modifier = Modifier.size(24.dp)
            )

            Text(" : ", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                modifier = Modifier
                    .clickable {
                        datePickerDialog.show()
                    }
                    .padding(4.dp)
                    .background(Color.Transparent),
            ) {
                Box(
                    modifier = Modifier
                        .width(202.dp)
                        .height(40.dp)
                        .background(Color.White)
                ) {
//                    if (isError) {
//                        Text(
//                            text = errorMessage,
//                            color = Color.Red,
//                            modifier = Modifier.padding(top = 4.dp),
//                            fontSize = 12.sp
//                        )
//                    }else {
                    Text(
                        text = displayDate,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterStart),
                        fontSize = 17.sp,
                        color = Color.Black
                    )
//                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}