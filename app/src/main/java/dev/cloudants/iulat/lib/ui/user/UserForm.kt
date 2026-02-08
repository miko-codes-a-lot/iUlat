package dev.cloudants.iulat.lib.ui.user

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import android.util.Base64
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.components.button.CustomButton
import dev.cloudants.iulat.lib.intent.UserIntent
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.viewmodels.AddressViewModel
import dev.cloudants.iulat.lib.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.InputStream
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
    viewModel: UserViewModel = hiltViewModel(),
    addressViewModel: AddressViewModel = hiltViewModel(),
    showUpload: Boolean = true
) {
    val state by viewModel.uiState.collectAsState()
    var isVerified by remember { mutableStateOf(targetUserDto?.isVerified ?: false) }
    val verifiedColor by animateColorAsState(if (isVerified) Color(0xFF0049AD) else Color.Transparent)
    val notVerifiedColor by animateColorAsState(if (!isVerified) Color.Red.copy(alpha = 0.8f) else Color.Transparent)
    val (chosenRole, setChosenRole) = remember {
        mutableStateOf(
            if (!showUpload) "Residence"
            else if (targetUserDto?.isAdmin == true) "Admin"
            else "Residence"
        )
    }
    val context = LocalContext.current
    var selectedIdUri by remember { mutableStateOf<Uri?>(null) }
    var selectedGender by remember { mutableStateOf(targetUserDto?.gender ?: "Unspecified") }

    val listOfLabel = mutableListOf(
        "First Name", "Middle Name", "Last Name", "Date Of Birth",
        "Address", "Mobile Number", "Email"
    )
    LaunchedEffect(showUpload) {
        if (!showUpload) {
            setChosenRole("Residence")
        }
    }
    if (includePassword || targetUserDto != null) {
        listOfLabel.add("Password")
    }
    LaunchedEffect(Unit) {
        addressViewModel.loadAddresses()
    }

    val statesValue = remember(targetUserDto) {
        (listOfLabel + listOf("Province", "Municipality", "Barangay", "Zone", "Latitude", "Longitude"))
            .associateWith { label ->
                mutableStateOf(
                    when(label) {
                        "First Name" -> targetUserDto?.firstName?.trim() ?: ""
                        "Middle Name" -> targetUserDto?.middleName?.trim() ?: ""
                        "Last Name" -> targetUserDto?.lastName?.trim() ?: ""
                        "Date Of Birth" -> targetUserDto?.dateOfBirth?.trim() ?: ""
                        "Address" -> targetUserDto?.address?.zone?.trim() ?: ""
                        "Province" -> targetUserDto?.address?.province ?: ""
                        "Municipality" -> targetUserDto?.address?.municipality ?: ""
                        "Barangay" -> targetUserDto?.address?.barangay ?: ""
                        "Zone" -> targetUserDto?.address?.zone ?: ""
                        "Latitude" -> targetUserDto?.address?.latitude?.toString() ?: "0.0"
                        "Longitude" -> targetUserDto?.address?.longitude?.toString() ?: "0.0"
                        "Mobile Number" -> targetUserDto?.mobileNumber?.trim() ?: ""
                        "Email" -> targetUserDto?.email?.trim() ?: ""
                        "Password" -> ""
//                        "Password" -> targetUserDto?.password?.trim() ?: ""
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
            statesValue["isVerified"]?.value = it.isVerified.toString()
//            statesValue["Password"]?.value = it.password
            Log.d("UserForm", "Editing user: ${it.username}")
            Log.d("UserForm", "Original hashed password: ${it.password}")
            statesValue["Password"]?.value = ""
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
        if (showUpload) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 12.dp)
                        .height(45.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color(0xFFF2F2F2)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(25.dp))
                            .background(if (!isVerified) notVerifiedColor else Color.Transparent)
                            .clickable { isVerified = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Not Verified",
                            color = if (!isVerified) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(25.dp))
                            .background(if (isVerified) verifiedColor else Color.Transparent)
                            .clickable { isVerified = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Verified",
                            color = if (isVerified) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
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
                        "Address" -> {
                            AddressSelector(
                                userAddress = targetUserDto?.address,
                                viewModel = addressViewModel,
                                onAddressSelected = { selected ->
                                    statesValue["Province"]?.value = selected.province
                                    statesValue["Municipality"]?.value = selected.municipality
                                    statesValue["Barangay"]?.value = selected.barangay
                                    statesValue["Zone"]?.value = selected.zone
                                    statesValue["Latitude"]?.value = selected.latitude.toString()
                                    statesValue["Longitude"]?.value = selected.longitude.toString()
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

        item {
            Text(
                text = "Select Gender:",
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
                listOf("Male", "Female").forEach { gender ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
                        )
                        Text(gender)
                    }
                }
            }
        }
        if (showUpload) {
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
                    val roles = if (!showUpload) listOf("Residence") else listOf("Admin", "Residence")
                    roles.forEach { role ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = chosenRole == role,
                                onClick = { setChosenRole(role) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
                            )
                            Text(text = role)
                        }
                    }
                }
            }
        }
//        if (showUpload) {
            item {
                UploadIdUI(
                    existingImageUrl = targetUserDto?.validId,
                    onImageSelected = { uri ->
                        selectedIdUri = uri
                    }
                )
            }
//        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
        item {
                Spacer(modifier = Modifier.height(24.dp))
            when {
                state.isLoading -> CircularProgressIndicator()
                else -> CustomButton(
                    text = "Submit",
                    onClick = {
                        val rawPassword = statesValue["Password"]?.value ?: ""
                        val finalPassword = rawPassword.ifBlank {
                            targetUserDto?.password ?: ""
                        }
                        var validId: String? = null
                        selectedIdUri?.let { uri ->
                            try {
                                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                                val byteArrayOutputStream = ByteArrayOutputStream()
                                inputStream?.copyTo(byteArrayOutputStream)
                                val byteArray = byteArrayOutputStream.toByteArray()
                                validId = Base64.encodeToString(byteArray, Base64.DEFAULT)
                            } catch (e: Exception) {
                                Log.e("UserForm", "Failed to encode image to Base64: ${e.message}")
                            }
                        }
                        val user = UserDto(
                            id =  targetUserDto?.id,
                            username = statesValue["Email"]?.value ?: "",
                            password = finalPassword,
                            firstName = statesValue["First Name"]?.value ?: "",
                            middleName = statesValue["Middle Name"]?.value,
                            lastName = statesValue["Last Name"]?.value ?: "",
                            email = statesValue["Email"]?.value ?: "",
                            mobileNumber = statesValue["Mobile Number"]?.value,
                            dateOfBirth = statesValue["Date Of Birth"]?.value ?: "",
                            gender = selectedGender,
                            address = AddressDto(
                                province = statesValue["Province"]?.value ?: "",
                                municipality = statesValue["Municipality"]?.value ?: "",
                                barangay = statesValue["Barangay"]?.value ?: "",
                                zone = statesValue["Zone"]?.value ?: "",
                                latitude = statesValue["Latitude"]?.value?.toDoubleOrNull() ?: 0.0,
                                longitude = statesValue["Longitude"]?.value?.toDoubleOrNull() ?: 0.0
                            ),
                            isVerified = isVerified,
                            type = "user",
                            isAdmin = chosenRole == "Admin",
                            isResidence = chosenRole == "Residence",
                            validId = validId ?: targetUserDto?.validId
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
    val base64Bitmap by remember(existingImageUrl) {
        mutableStateOf(
            if (!existingImageUrl.isNullOrEmpty() && !existingImageUrl.startsWith("http") && !existingImageUrl.startsWith("content://")) {
                try {
                    val decodedBytes = Base64.decode(existingImageUrl, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) {
                    Log.e("UploadIdUI", "Failed to decode base64 image: ${e.message}")
                    null
                }
            } else null
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImgUri = uri
            onImageSelected(uri)
        }
    )

    val displayImage: Any? = when {
        selectedImgUri != null -> selectedImgUri
        base64Bitmap != null -> base64Bitmap!!.asImageBitmap()
        existingImageUrl?.startsWith("http") == true || existingImageUrl?.startsWith("https") == true -> existingImageUrl
        else -> null
    }

    Box(
        modifier = Modifier
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
            when (displayImage) {
                is ImageBitmap -> {
                    Image(
                        bitmap = displayImage,
                        contentDescription = "Valid ID",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                is String, is Uri -> {
                    AsyncImage(
                        model = displayImage,
                        contentDescription = "Valid ID",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Text(
                        "Tap to Upload ID",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
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
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { value ->
                onValueChange(value.trim())
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(text = textFieldLabel, fontFamily = FontFamily.SansSerif, color = Color(0xFF0049AD))
            },
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
                            tint = Color(0xFF0049AD),
                            contentDescription = null
                        )
                    }
                }
            },
            textStyle = TextStyle(
                color = Color(0xFF0049AD),
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color(0xFF0049AD),
                cursorColor = Color(0xFF0049AD),
            ),
            singleLine = true
        )
    }
}

@Composable
fun DatePickerField(
    label: String,
    dateValue: String,
    onDateChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(dateValue) {
        if (dateValue.isNotBlank()) {
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd"
            )
            for (f in formats) {
                try {
                    val parsed = SimpleDateFormat(f, Locale.getDefault()).parse(dateValue)
                    if (parsed != null) {
                        calendar.time = parsed
                        break
                    }
                } catch (_: Exception) {}
            }
        }
    }

    val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val displayDate = try {
        if (dateValue.isBlank()) "Select Date"
        else SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            .parse(dateValue)?.let { displayFormat.format(it) } ?: "Select Date"
    } catch (_: Exception) {
        "Select Date"
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val isoFormat = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    Locale.getDefault()
                )
                isoFormat.timeZone = TimeZone.getTimeZone("UTC")
                onDateChange(isoFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clickable { datePickerDialog.show() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label :",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            painter = painterResource(id = R.drawable.calendar_icon),
            contentDescription = "Calendar Icon",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF0049AD)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Surface(
            modifier = Modifier
                .clickable { datePickerDialog.show() }
                .height(40.dp)
                .width(200.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(
                    text = displayDate,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF0049AD)
                )
            }
        }
    }
}

@Composable
fun AddressSelector(
    userAddress: AddressDto? = null,
    viewModel: AddressViewModel,
    onAddressSelected: (AddressDto) -> Unit
) {
    val addresses = viewModel.addressList
    var expanded by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf(userAddress) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Address",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                }
        ) {
            OutlinedTextField(
                value = selectedAddress?.zone?.let {
                    if (selectedAddress?.barangay?.isNotEmpty() == true) "${it}, ${selectedAddress?.barangay}"
                    else it
                } ?: "Select Address",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    }
                    .clickable { expanded = !expanded },
                readOnly = true,
                trailingIcon = {
                    Icon(icon, "Dropdown Icon", modifier = Modifier.clickable { expanded = !expanded })
                },
                textStyle = TextStyle(
                    color = Color(0xFF0049AD),
                    fontSize = 16.sp,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .padding(start = 10.dp, end = 10.dp)
                    .width(textFieldSize.width.dp)
                    .heightIn(max = 5 * 48.dp)
            ) {
                addresses.forEach { address ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(start = 10.dp, end = 10.dp),
                        text = {
                            Text(
                                text = if (address.barangay.isNotEmpty())
                                    "${address.zone}, ${address.barangay}"
                                else address.zone,
                                color = Color(0xFF0049AD),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                        onClick = {
                            selectedAddress = address
                            onAddressSelected(address)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}