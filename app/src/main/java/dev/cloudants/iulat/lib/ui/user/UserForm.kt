package dev.cloudants.iulat.lib.ui.user

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.cloudants.iulat.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserForm(
    title: String,
    onSubmit: (UserSample) -> Unit
) {
    val listOfLabel = listOf(
        "First Name", "Middle Name", "Last Name", "Date Of Birth",
        "Address", "Mobile Number", "Email", "Password"
    )

    val statesValue = remember {
        listOfLabel.associateWith { mutableStateOf("") }
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
                            DatePickerField(label) { selectedDate ->
                                statesValue[label]?.value = selectedDate
                            }
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
            UploadIdUI()
        }

        item {
            Button(
                onClick = { /* No submit logic, display-only */ },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF136204),
                    contentColor = Color.White
                ),
            ) {
                Text("Submit", fontSize = 18.sp)
            }
        }

        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

@Composable
fun UploadIdUI() {
    val context = LocalContext.current
    var selectedImgUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImgUri = uri }
    )

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
                .height(250.dp)
                .fillMaxWidth()
                .clip(RectangleShape)
                .background(if (selectedImgUri != null) Color.White else Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImgUri != null) {
                AsyncImage(
                    model = selectedImgUri,
                    contentDescription = "Valid ID",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
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

@SuppressLint("RememberReturnType")
@Composable
fun DatePickerField(
    label: String,
    onDateChange: (String) -> Unit,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = "$dayOfMonth/${month + 1}/$year"
                onDateChange(selected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { datePickerDialog.show() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Select Date",
            fontSize = 16.sp,
            color = Color.Black
        )
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
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}
