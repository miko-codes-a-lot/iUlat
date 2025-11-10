package dev.cloudants.iulat.lib.ui.user

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.cloudants.iulat.lib.models.entities.UserDto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun UserPreview(
    title: String,
    user: UserDto,
    onSave: suspend (UserDto) -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val isSaving = remember { mutableStateOf(false) }

    val userInfo = listOf(
        "First Name" to user.firstName,
        "Middle Name" to (user.middleName ?: ""),
        "Last Name" to user.lastName,
        "Address" to (user.address?.province ?: ""),
        "Mobile Number" to (user.mobileNumber ?: ""),
        "Date of Birth" to formatDate(user.dateOfBirth),
        "Email" to user.email
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        userInfo.forEach { (label, value) ->
            InfoRow(label, value)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (!isSaving.value) {
                    isSaving.value = true
                    Log.d("UserPreview", "Saving user: $user")
                    coroutineScope.launch {
                        try {
                            onSave(user)
                            Log.d("UserPreview", "User save request completed")
                        } catch (e: Exception) {
                            Log.e("UserPreview", "Error saving user: ${e.message}", e)
                        } finally {
                            isSaving.value = false
                        }
                    }
                }
            },
            enabled = !isSaving.value,
            modifier = Modifier
                .width(320.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0049AD),
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (isSaving.value) "Saving..." else "Confirm",
                fontSize = 17.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onCancel) {
            Text(
                text = "Cancel",
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "$label:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    }
}

fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "Select Date"
    val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val possibleFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd"
    )

    for (pattern in possibleFormats) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val parsed = parser.parse(dateString)
            if (parsed != null) {
                return displayFormat.format(parsed)
            }
        } catch (_: Exception) {}
    }

    return "Select Date"
}

